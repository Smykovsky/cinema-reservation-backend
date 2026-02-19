package pl.smyk.authservice.exception;

public class ResetTokenExpiredException extends RuntimeException {
    public ResetTokenExpiredException(String message) {
        super(message);
    }
}
