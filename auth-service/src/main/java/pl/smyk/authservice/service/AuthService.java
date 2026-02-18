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
import pl.smyk.authservice.exception.AccountLockedException;
import pl.smyk.authservice.exception.PasswordNotMatchException;
import pl.smyk.authservice.exception.UserAlreadyExistsException;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.model.Role;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final TotpService totpService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Użytkownik z takim adresem email już istnieje!");
        }

        if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            throw new PasswordNotMatchException("Podane hasła się nie zgadzają!");
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(List.of(Role.USER, Role.OPERATOR))
                .isAccountNonLocked(true)
                .build();

        userService.saveUser(user);

        return AuthenticationResponse.builder()
                .message("Pomyślnie utworzono konto!")
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException("Konto zostało zablokowane!");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (user.isTotpEnabled()) {
            totpService.validateTotpForLogin(user, request.getCode());
        }

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