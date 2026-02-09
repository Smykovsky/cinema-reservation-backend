package pl.smyk.cinemaservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "screening_seats")
@IdClass(ScreeningSeatId.class) // Composite key
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningSeat {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Column(length = 255)
    private String reservedBy;

    @Column
    private Instant reservedUntil;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public boolean isAvailable() {
        if (status == SeatStatus.AVAILABLE) {
            return true;
        }

        if (status == SeatStatus.RESERVED && reservedUntil != null) {
            return Instant.now().isAfter(reservedUntil);
        }

        return false;
    }

    public void reserve(String bookingId, Instant until) {
        if (!isAvailable()) {
            throw new IllegalStateException("Seat is not available");
        }
        this.status = SeatStatus.RESERVED;
        this.reservedBy = bookingId;
        this.reservedUntil = until;
    }

    public void confirm() {
        if (status != SeatStatus.RESERVED) {
            throw new IllegalStateException("Can only confirm reserved seats");
        }
        this.status = SeatStatus.SOLD;
        this.reservedUntil = null;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.reservedBy = null;
        this.reservedUntil = null;
    }
}