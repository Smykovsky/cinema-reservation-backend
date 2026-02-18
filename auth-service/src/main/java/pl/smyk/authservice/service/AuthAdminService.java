package pl.smyk.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.exception.UserNotFoundException;
import pl.smyk.authservice.model.User;

@Service
@RequiredArgsConstructor
public class AuthAdminService {
    private final UserService userService;

    public void banUser(String userEmail) {
        User user = this.userService.findByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException("Użytkownik z takim adresem email nie istnieje!");
        }

        user.setAccountNonLocked(false);
        this.userService.save(user);
    }

    public void unBanUser(String userEmail) {
        User user = this.userService.findByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException("Użytkownik z takim adresem email nie istnieje!");
        }
        user.setAccountNonLocked(true);
        this.userService.save(user);
    }

}
