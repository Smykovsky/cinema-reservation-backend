package pl.smyk.authservice.exception;

public class TotpNotConfiguredException extends RuntimeException {
    public TotpNotConfiguredException(String message) {
        super(message);
    }
}
