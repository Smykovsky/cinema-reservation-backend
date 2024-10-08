package pl.smyk.authservice.dto;

import lombok.Data;
import pl.smyk.authservice.model.Role;

import java.util.List;

@Data
public class CustomerDto {
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
