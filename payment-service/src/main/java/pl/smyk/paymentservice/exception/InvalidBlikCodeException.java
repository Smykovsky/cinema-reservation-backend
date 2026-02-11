package pl.smyk.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This exception is a more specific type of PaymentValidationException
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBlikCodeException extends PaymentValidationException {
    public InvalidBlikCodeException(String message) {
        super(message);
    }
}
