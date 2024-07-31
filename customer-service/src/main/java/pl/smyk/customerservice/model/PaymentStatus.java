package pl.smyk.customerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PAID, WAITING;
}

