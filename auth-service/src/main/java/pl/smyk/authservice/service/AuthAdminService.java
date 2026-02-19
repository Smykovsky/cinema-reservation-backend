package pl.smyk.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.exception.UserNotFoundException;
import pl.smyk.authservice.mapper.UserMapper;
import pl.smyk.authservice.model.Role;
import pl.smyk.authservice.model.User;
import pl.smyk.common.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthAdminService {
    private final UserService userService;

    public List<UserDto> getAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        return allUsers.stream()
                .filter(u -> u.getRoles().contains(Role.USER) || u.getRoles().contains(Role.OPERATOR))
                .filter(u -> !(u.getRoles().contains(Role.USER) && u.getRoles().contains(Role.ADMIN)))
                .map(UserMapper.INSTANCE::userToUserDto)
                .toList();
    }

    public List<UserDto> getAllAdminUsers() {
        List<User> allUsers = userService.findAllUsers();
        return allUsers.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.equals(Role.ADMIN)))
                .map(UserMapper.INSTANCE::userToUserDto)
                .toList();
    }

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
