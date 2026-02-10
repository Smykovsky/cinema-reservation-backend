package pl.smyk.cinemaservice.exception;

public class HallHasNoSeatsException extends RuntimeException {
    public HallHasNoSeatsException(String message) {
        super(message);
    }
}