package pl.smyk.cinemaservice.exception;

public class ScreeningAlreadyExistsException extends RuntimeException {
    public ScreeningAlreadyExistsException(String message) {
        super(message);
    }
}