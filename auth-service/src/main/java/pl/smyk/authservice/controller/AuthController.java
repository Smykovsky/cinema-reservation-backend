package pl.smyk.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.authservice.config.jwt.JwtUtil;
import pl.smyk.authservice.dto.AuthenticationResponse;
import pl.smyk.authservice.dto.LoginRequest;
import pl.smyk.authservice.dto.RegisterRequest;
import pl.smyk.authservice.model.Customer;
import pl.smyk.authservice.service.AuthService;
import pl.smyk.authservice.service.CustomerService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CustomerService customerService;


  @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterRequest request) {
        if (customerService.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("Użytkonik o podanym adresie email już istnieje!");
        }

        if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            return ResponseEntity.status(400).body("Podane hasła nie zgadzają się!");
        }

        AuthenticationResponse register = authService.register(request);
        return ResponseEntity.ok(register);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequest request) {
        Optional<Customer> optionalCustomer = customerService.findByEmail(request.getEmail());
        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        AuthenticationResponse login = authService.login(request);
        return ResponseEntity.ok(login);
    }

  @GetMapping("/validate")
  public String validateToken(@RequestParam("token") String token) {
    authService.validateToken(token);
    return "Token is valid";
  }
}
