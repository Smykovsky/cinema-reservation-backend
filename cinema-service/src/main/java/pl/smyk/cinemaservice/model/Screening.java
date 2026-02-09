package pl.smyk.cinemaservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screenings",
        indexes = {
                @Index(name = "idx_screening_movie", columnList = "movie_id"),
                @Index(name = "idx_screening_hall", columnList = "hall_id"),
                @Index(name = "idx_screening_time", columnList = "start_time")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long movieId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal vipPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal wheelchairPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ScreeningStatus status = ScreeningStatus.SCHEDULED;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScreeningSeat> screeningSeats = new ArrayList<>();

    // Business logic
    public boolean hasStarted() {
        return Instant.now().isAfter(startTime);
    }

    public boolean hasEnded() {
        return Instant.now().isAfter(endTime);
    }

    public boolean isActive() {
        return status == ScreeningStatus.SCHEDULED || status == ScreeningStatus.ONGOING;
    }

    public BigDecimal getPriceForSeatType(SeatType seatType) {
        return switch (seatType) {
            case VIP -> vipPrice;
            case WHEELCHAIR -> wheelchairPrice != null ? wheelchairPrice : basePrice;
            case STANDARD -> basePrice;
        };
    }
}