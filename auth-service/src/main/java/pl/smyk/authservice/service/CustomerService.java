package pl.smyk.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.dto.UserDto;
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
}
