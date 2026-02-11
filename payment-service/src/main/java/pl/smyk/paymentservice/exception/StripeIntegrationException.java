package pl.smyk.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StripeIntegrationException extends RuntimeException {
    public StripeIntegrationException(String message) {
        super(message);
    }

    public StripeIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
