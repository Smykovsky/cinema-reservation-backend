package pl.smyk.customerservice.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.smyk.customerservice.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    @Nullable
    private String id;
    private String customerEmail;
    private Long roomNumber;
    private MovieDto movie;
    private LocalDateTime selectedPlayTime;
    private List<SeatDto> seats;
    private Double totalPrice;
    private PaymentStatus paymentStatus;
}
