package pl.smyk.customerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public enum PlayTime {
    HOUR_12(LocalTime.of(12, 0)),
    HOUR_14(LocalTime.of(14, 0)),
    HOUR_16(LocalTime.of(16, 0)),
    HOUR_18(LocalTime.of(18, 0)),
    HOUR_20(LocalTime.of(20, 0)),
    HOUR_22(LocalTime.of(22, 0));


    private final LocalTime playTime;
}