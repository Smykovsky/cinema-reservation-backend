package pl.smyk.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.smyk.authservice.service.AuthAdminService;
import pl.smyk.common.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/api/auth/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AuthAdminController {
    private final AuthAdminService authAdminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> allUsers = authAdminService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserDto>> getAllAdminUsers() {
        List<UserDto> allAdmins = authAdminService.getAllAdminUsers();
        return ResponseEntity.ok(allAdmins);
    }

    @PostMapping("/ban/{userEmail}")
    public void banUser(@PathVariable String userEmail) {
        authAdminService.banUser(userEmail);
    }

    @PostMapping("/unban/{userEmail}")
    public void unbanUser(@PathVariable String userEmail) {
        authAdminService.unBanUser(userEmail);
    }
}
