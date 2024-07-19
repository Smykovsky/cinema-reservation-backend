package pl.smyk.authservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.smyk.authservice.mapper.CustomerMapper;
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
//        template.dropCollection(Customer.class);
//        template.createCollection(Customer.class);
//
//        Customer c1 = new Customer("smyku1232@wp.pl", passwordEncoder.encode("test1") , "Kamil", "Smyk");
//        Customer c2 = new Customer("smyku1111232@wp.pl", passwordEncoder.encode("test11"), "Kamil", "Smyk");
//        c1.setRoles(List.of(Role.CUSTOMER, Role.OPERATOR));
//        c2.setRoles(List.of(Role.CUSTOMER));
//        template.insert(c1);
//        template.insert(c2);

        System.out.println(customerService.findCustomerById("669a28a467d9c934cbdd6327"));


    }
}
