package pl.smyk.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smyk.authservice.dto.CustomerDto;
import pl.smyk.authservice.mapper.CustomerMapper;
import pl.smyk.authservice.model.Customer;
import pl.smyk.authservice.repository.CustomerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Optional<CustomerDto> findCustomerById(String id) {
        Optional<Customer> byId = customerRepository.findById(id);

        return Optional.ofNullable(CustomerMapper.INSTANCE.customerToCustomerDto(byId.get()));
    }

    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
