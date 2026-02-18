package pl.smyk.authservice.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
