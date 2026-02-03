package pl.smyk.authservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private boolean totpEnabled;
}