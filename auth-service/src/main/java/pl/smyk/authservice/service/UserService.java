package pl.smyk.authservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.dto.UserDto;
import pl.smyk.authservice.dto.UserUpdateRequest;
import pl.smyk.authservice.exception.UserNotFoundException;
import pl.smyk.authservice.mapper.UserMapper;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<UserDto> findByUserId(Long id) {
        Optional<User> byId = userRepository.findById(id);

        return Optional.ofNullable(UserMapper.INSTANCE.userToUserDto(byId.get()));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Nie ma takiego użytkownika w bazie"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found"));

        Optional.ofNullable(request.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(request.getFirstName())
                .ifPresent(user::setFirstName);

        Optional.ofNullable(request.getLastName())
                .ifPresent(user::setLastName);
    }
}
