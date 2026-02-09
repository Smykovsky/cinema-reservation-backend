package pl.smyk.cinemaservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "halls",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cinema_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false, length = 100)
    private String name; // "Sala 1", "IMAX", "VIP Lounge"

    @Column(nullable = false)
    private Integer totalSeats;

    @ElementCollection
    @CollectionTable(name = "hall_features", joinColumns = @JoinColumn(name = "hall_id"))
    @Column(name = "feature")
    private List<String> features; // ["IMAX", "4DX", "Dolby Atmos"]

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Relacja 1:N z Seats
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    // Relacja 1:N z Screenings
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Screening> screenings = new ArrayList<>();

    // Helper methods
    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setHall(this);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
        seat.setHall(null);
    }
}