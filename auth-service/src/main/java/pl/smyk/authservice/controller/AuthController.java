package pl.smyk.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.smyk.authservice.dto.*;
import pl.smyk.authservice.mapper.UserMapper;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.service.AuthService;
import pl.smyk.authservice.service.TotpService;
import pl.smyk.authservice.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final TotpService totpService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUserData(@RequestHeader("X-User-Email") String email) {
        User user = userService.findByEmail(email);
        UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        userService.updateUser(userId, request);
    }

    @PostMapping("/totp/enable")
    public ResponseEntity<TotpEnableResponse> enableTotp(@RequestHeader("X-User-Email") String email) {
        TotpEnableResponse response = totpService.enableTotp(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/totp/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyTotp(@RequestHeader("X-User-Email") String email,
                           @RequestBody TotpVerifyRequest request) {
        totpService.verifyAndActivateTotp(email, request.getCode());
    }

    @PostMapping("/totp/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableTotp(@RequestHeader("X-User-Email") String email) {
        totpService.disableTotp(email);
    }
}