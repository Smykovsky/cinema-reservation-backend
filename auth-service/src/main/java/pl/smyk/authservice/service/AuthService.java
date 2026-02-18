package pl.smyk.authservice.service;

import jakarta.transaction.Transactional;
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
import pl.smyk.authservice.dto.ResetPasswordRequest;
import pl.smyk.authservice.exception.*;
import pl.smyk.authservice.kafka.AuthEventProducer;
import pl.smyk.authservice.model.PasswordResetToken;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.model.Role;
import pl.smyk.authservice.repository.PasswordResetTokenRepository;
import pl.smyk.common.dto.PasswordResetEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final TotpService totpService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthEventProducer authEventProducer;
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
                .roles(List.of(Role.USER))
                .isAccountNonLocked(true)
                .build();

        userService.saveUser(user);

        return AuthenticationResponse.builder()
                .message("Pomyślnie utworzono konto!")
                .build();
    }

    public AuthenticationResponse generateResetToken(String email) {

        User user = userService.findByEmail(email);

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = passwordEncoder.encode(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .user(user)
                .build();

        passwordResetTokenRepository.save(resetToken);

        PasswordResetEvent event = PasswordResetEvent.builder()
                .email(user.getEmail())
                .token(rawToken)
                .expiresAt(resetToken.getExpiresAt())
                .build();

        authEventProducer.sendPasswordResetEvent(event);

        return AuthenticationResponse.builder().message("Na podany adres email wysłaliśmy link do resetu hasła").build();
    }

    @Transactional
    public AuthenticationResponse resetPassword(ResetPasswordRequest request) {

        if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            throw new IllegalArgumentException("Hasła nie są identyczne");
        }

        PasswordResetToken token = passwordResetTokenRepository.findAll().stream()
                .filter(t -> passwordEncoder.matches(request.getToken(), t.getTokenHash()))
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Token jest nieważny lub wygasł"));


        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);
        return AuthenticationResponse.builder().message("Pomyślnie ustawiono hasło!").build();
    }


    public AuthenticationResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!user.isAccountNonLocked()) {
            throw new AccountLockedException("Konto zostało zablokowane!");
        }

        authenticationManager.authenticate(
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