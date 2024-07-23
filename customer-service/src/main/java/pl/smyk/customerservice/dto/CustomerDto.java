package pl.smyk.customerservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDto {
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
