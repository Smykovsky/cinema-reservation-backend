package pl.smyk.cinemaservice.exception;

public class HallNotFoundException extends RuntimeException {
    public HallNotFoundException(String message) {
        super(message);
    }
}