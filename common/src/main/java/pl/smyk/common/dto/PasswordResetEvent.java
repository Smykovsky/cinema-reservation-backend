package pl.smyk.common.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetEvent {
    private String email;
    private String token;
    private LocalDateTime expiresAt;
}
