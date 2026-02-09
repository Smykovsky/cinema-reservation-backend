package pl.smyk.cinemaservice.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ScreeningSeatId implements Serializable {

    private Long screening; // musi mieć taką samą nazwę jak pole w ScreeningSeat
    private Long seat;      // musi mieć taką samą nazwę jak pole w ScreeningSeat
}