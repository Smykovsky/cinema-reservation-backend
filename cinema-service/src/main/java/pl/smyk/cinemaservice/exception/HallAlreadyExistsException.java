package pl.smyk.cinemaservice.exception;

public class HallAlreadyExistsException extends RuntimeException {
    public HallAlreadyExistsException(String message) {
        super(message);
    }
}