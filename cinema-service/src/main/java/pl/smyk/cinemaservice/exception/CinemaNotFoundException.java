package pl.smyk.cinemaservice.exception;

public class CinemaNotFoundException extends RuntimeException {
    public CinemaNotFoundException(String message) {
        super(message);
    }
}