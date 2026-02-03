package pl.smyk.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.config.jwt.JwtUtil;
import pl.smyk.authservice.dto.AuthenticationResponse;
import pl.smyk.authservice.dto.LoginRequest;
import pl.smyk.authservice.dto.RegisterRequest;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.model.Role;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        if (userService.findByEmail(request.getEmail()) != null) {
            return AuthenticationResponse.builder()
                    .message("Istnieje użytkownik z takim adresem email!")
                    .build();
        } else if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            return AuthenticationResponse.builder()
                    .message("Podane hasła nie są takie same!")
                    .build();
        } else {
            var user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .roles(List.of(Role.USER, Role.OPERATOR))
                    .build();
            User savedUser = userService.saveUser(user);
            return AuthenticationResponse.builder()
                    .message("Pomyślnie utworzono konto!")
                    .build();
        }
    }

    public AuthenticationResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        System.out.println(authentication.isAuthenticated());

        User user = userService.findByEmail(request.getEmail());
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Pomyślnie zalogowano!")
                .build();
    }
    public void validateToken(String token) {
      jwtUtil.validateToken(token);
    }
}
