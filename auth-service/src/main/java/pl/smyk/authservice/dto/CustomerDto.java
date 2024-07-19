package pl.smyk.authservice.dto;

import lombok.Data;

@Data
public class CustomerDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
