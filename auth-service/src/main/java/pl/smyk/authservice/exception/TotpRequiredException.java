package pl.smyk.authservice.exception;

public class TotpRequiredException extends RuntimeException {
    public TotpRequiredException(String message) {
        super(message);
    }
}