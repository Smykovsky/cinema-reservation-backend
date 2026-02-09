package pl.smyk.cinemaservice.exception;

public class CinemaAlreadyExistsException extends RuntimeException {
    public CinemaAlreadyExistsException(String message) {
        super(message);
    }
}