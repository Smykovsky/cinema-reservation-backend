package pl.smyk.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.authservice.config.jwt.JwtUtil;
import pl.smyk.authservice.dto.*;
import pl.smyk.authservice.mapper.UserMapper;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.service.AuthService;
import pl.smyk.authservice.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
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
        Optional<User> user = userService.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("Nie ma takiego użytkownika w naszej bazie!");
        }

        AuthenticationResponse loginResponse = authService.login(request);
        ApiResponse<AuthenticationResponse> response = ApiResponse.of("Pomyślnie zalogowano!", 200, loginResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null && !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Inavlid token!");
        }

        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
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
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        if (authorizationHeader == null && !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Inavlid token!");
        }

        this.userService.updateUser(userId, request);
        ApiResponse<Object> response = ApiResponse.of("Pomyślnie zaktualizowano profil użytkownika", 200, null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
