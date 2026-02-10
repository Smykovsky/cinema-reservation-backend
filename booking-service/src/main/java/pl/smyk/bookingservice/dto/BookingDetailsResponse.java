package pl.smyk.bookingservice.dto;

import lombok.Builder;
import lombok.Data;
import pl.smyk.bookingservice.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BookingDetailsResponse {
    private Long id;
    private Long userId;
    private Long screeningId;
    private String bookingNumber;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private List<BookingSeatResponse> seats;
}