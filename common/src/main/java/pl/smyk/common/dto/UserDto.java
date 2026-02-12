package pl.smyk.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private boolean totpEnabled;
}