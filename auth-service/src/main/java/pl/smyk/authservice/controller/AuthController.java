package pl.smyk.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.authservice.config.jwt.JwtUtil;
import pl.smyk.authservice.dto.*;
import pl.smyk.authservice.mapper.UserMapper;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.service.AuthService;
import pl.smyk.authservice.service.TotpService;
import pl.smyk.authservice.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final TotpService totpService;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse registerResponse = authService.register(request);
        ApiResponse<AuthenticationResponse> response = ApiResponse.of("Pomyślnie utworzono konto", 200, registerResponse);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (user.isTotpEnabled()) {
            if (request.getCode() == 0) {
                ApiResponse<Object> response = ApiResponse.of("Kod TOTP jest wymagany.", 400);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
            if (!totpService.verifyCode(user.getTotpSecret(), request.getCode())) {
                ApiResponse<Object> response = ApiResponse.of("Wprowadzono niepoprawny kod TOTP.", 401);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        }

        AuthenticationResponse loginResponse = authService.login(request);
        ApiResponse<AuthenticationResponse> response = ApiResponse.of("Pomyślnie zalogowano!", 200, loginResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(@RequestHeader("X-User-Email") String email) {
        User user = userService.findByEmail(email);

        UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);
        ApiResponse<UserDto> response = ApiResponse.of("Pomyślnie odczytano dane użytkownika", 200, userDto);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        this.userService.updateUser(userId, request);
        ApiResponse<Object> response = ApiResponse.of("Pomyślnie zaktualizowano profil użytkownika", 200, null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/totp/enable")
    public ResponseEntity<?> enableTotp(@RequestHeader("X-User-Email") String email) {
        User user = userService.findByEmail(email);

        String secret = totpService.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(true);
        userService.save(user);
        String qrCode = totpService.generateQrCode(secret, user.getEmail());
        return ResponseEntity.ok(new TotpEnableResponse(secret, qrCode));
        //poprawka
    }

    @PostMapping("/totp/verify")
    public ResponseEntity<?> verifyTotp(@RequestHeader("X-User-Email") String email, @RequestBody TotpVerifyRequest request) {
        User user = userService.findByEmail(email);

        if (!totpService.verifyCode(user.getTotpSecret(), request.getCode())) {
            ApiResponse<Object> response = ApiResponse.of("Wprowadzono niepoprawny kod TOTP.", 400);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        user.setTotpEnabled(true);
        userService.save(user);
        ApiResponse<Object> response = ApiResponse.of("Pomyślnie włączono podwójną autentykację (TOTP).", 200);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/totp/disable")
    public ResponseEntity<?> disableTotp(@RequestHeader("X-User-Email") String email) {
        User user = userService.findByEmail(email);

        user.setTotpEnabled(false);
        user.setTotpSecret(null);
        userService.save(user);
        ApiResponse<Object> response = ApiResponse.of("Pomyślnie wyłączono podwójną autentykację (TOTP).", 200);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}