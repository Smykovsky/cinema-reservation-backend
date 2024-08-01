package pl.smyk.customerservice.dto.response;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationResponse {
    private String reservationId;
    private String customerEmail;
    private String errorReason;
    private String message;
}
