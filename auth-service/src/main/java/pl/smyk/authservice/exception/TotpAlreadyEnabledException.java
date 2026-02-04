package pl.smyk.authservice.exception;

public class TotpAlreadyEnabledException extends RuntimeException {
    public TotpAlreadyEnabledException(String message) {
        super(message);
    }
}
