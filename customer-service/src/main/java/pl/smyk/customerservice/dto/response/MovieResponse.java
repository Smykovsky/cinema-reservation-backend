package pl.smyk.customerservice.dto.response;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class MovieResponse {
    private int status;
    private String message;
}
