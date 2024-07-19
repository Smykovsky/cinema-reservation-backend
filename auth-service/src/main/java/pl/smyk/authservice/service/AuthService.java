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
import pl.smyk.authservice.model.Customer;
import pl.smyk.authservice.model.Role;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        if (customerService.findByEmail(request.getEmail()).isPresent()) {
            return AuthenticationResponse.builder()
                    .message("Istnieje użytkownik z takim adresem email!")
                    .build();
        } else if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            return AuthenticationResponse.builder()
                    .message("Podane hasła nie są takie same!")
                    .build();
        } else {
            var customer = Customer.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .roles(List.of(Role.valueOf(Role.CUSTOMER.name())))
                    .build();
            Customer savedCustomer = customerService.saveCustomer(customer);
            return AuthenticationResponse.builder()
                    .message("Pomyślnie utworzono konto!")
                    .build();
        }
    }

    public AuthenticationResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        System.out.println(authentication.isAuthenticated());

        Customer customer = customerService.findByEmail(request.getEmail()).orElseThrow();
        String accessToken = jwtUtil.generateToken(customer);
        String refreshToken = jwtUtil.generateRefreshToken(customer);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .message("Pomyślnie zalogowano!")
                .build();
    }
}
