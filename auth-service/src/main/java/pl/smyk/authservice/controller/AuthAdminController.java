package pl.smyk.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.smyk.authservice.service.AuthAdminService;

@RestController("/api/auth/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AuthAdminController {
    private final AuthAdminService authAdminService;

    @PostMapping("/ban/{userEmail}")
    public void banUser(@PathVariable String userEmail) {
        authAdminService.banUser(userEmail);
    }

    @PostMapping("/unban/{userEmail}")
    public void unbanUser(@PathVariable String userEmail) {
        authAdminService.unBanUser(userEmail);
    }
}
