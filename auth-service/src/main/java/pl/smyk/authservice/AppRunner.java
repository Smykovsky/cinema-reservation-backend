package pl.smyk.authservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.smyk.authservice.model.Customer;
import pl.smyk.authservice.model.Role;
import pl.smyk.authservice.service.CustomerService;

import java.util.List;

@Component
public class AppRunner implements CommandLineRunner {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerService customerService;

    @Override
    public void run(String... args) throws Exception {
        template.dropCollection(Customer.class);
        template.createCollection(Customer.class);
        Customer c1 = Customer.builder()
          .firstName("Kamil")
          .lastName("Smyk")
          .email("smyku1232@wp.pl")
          .password(passwordEncoder.encode("test"))
          .roles(List.of(Role.CUSTOMER))
          .build();

        Customer c2 = Customer.builder()
                .firstName("Kamill")
                .lastName("Smykk")
                .email("kamil.smyk00@gmail.com")
                .password(passwordEncoder.encode("test"))
                .roles(List.of(Role.CUSTOMER))
                .build();

        template.insert(c1);
        template.insert(c2);


    }
}
