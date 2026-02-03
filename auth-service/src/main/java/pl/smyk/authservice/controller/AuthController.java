package pl.smyk.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        if (userService.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("Użytkownik o podanym adresie email już istnieje!");
        }

        if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            return ResponseEntity.status(400).body("Podane hasła nie zgadzają się!");
        }

        AuthenticationResponse registerResponse = authService.register(request);
        ApiResponse<AuthenticationResponse> response = ApiResponse.of("Pomyślnie utworzono konto", 200, registerResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userService.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Nie ma takiego użytkownika w naszej bazie!");
        }

        User user = userOptional.get();
        if (user.isTotpEnabled()) {
            if (request.getCode() == 0) {
                return ResponseEntity.status(400).body("TOTP code is required.");
            }
            if (!totpService.verifyCode(user.getTotpSecret(), request.getCode())) {
                return ResponseEntity.status(401).body("Invalid TOTP code.");
            }
        }

        AuthenticationResponse loginResponse = authService.login(request);
        ApiResponse<AuthenticationResponse> response = ApiResponse.of("Pomyślnie zalogowano!", 200, loginResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(@RequestHeader("X-User-Email") String email) {
        Optional<User> byEmail = userService.findByEmail(email);
        if (byEmail.isEmpty()) {
            ApiResponse<UserDto> response = ApiResponse.of("Błąd podczas odczytywania danych użytkownika", 404, null);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        User user = byEmail.get();
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
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = userOptional.get();
        String secret = totpService.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(true);
        userService.save(user);
        String qrCode = totpService.generateQrCode(secret, user.getEmail());
        return ResponseEntity.ok(new TotpEnableResponse(secret, qrCode));
    }

    @PostMapping("/totp/verify")
    public ResponseEntity<?> verifyTotp(@RequestHeader("X-User-Email") String email, @RequestBody TotpVerifyRequest request) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = userOptional.get();
        if (!totpService.verifyCode(user.getTotpSecret(), request.getCode())) {
            return ResponseEntity.status(401).body("Invalid TOTP code.");
        }
        user.setTotpEnabled(true);
        userService.save(user);
        return ResponseEntity.ok("TOTP enabled successfully.");
    }

    @PostMapping("/totp/disable")
    public ResponseEntity<?> disableTotp(@RequestHeader("X-User-Email") String email) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = userOptional.get();
        user.setTotpEnabled(false);
        user.setTotpSecret(null);
        userService.save(user);
        return ResponseEntity.ok("TOTP disabled successfully.");
    }
}