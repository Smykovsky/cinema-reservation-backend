package pl.smyk.authservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.dto.UserDto;
import pl.smyk.authservice.dto.UserUpdateRequest;
import pl.smyk.authservice.exception.UserNotFoundException;
import pl.smyk.authservice.mapper.UserMapper;
import pl.smyk.authservice.model.User;
import pl.smyk.authservice.repository.CustomerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Optional<UserDto> findCustomerById(Long id) {
        Optional<User> byId = customerRepository.findById(id);

        return Optional.ofNullable(UserMapper.INSTANCE.userToUserDto(byId.get()));
    }

    public Optional<User> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public User saveCustomer(User customer) {
        return customerRepository.save(customer);
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found"));

        Optional.ofNullable(request.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(request.getFirstName())
                .ifPresent(user::setFirstName);

        Optional.ofNullable(request.getLastName())
                .ifPresent(user::setLastName);
    }
}
