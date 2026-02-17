package pl.smyk.bookingservice.exception;

public class UserBookingsNotFoundException extends RuntimeException {
    public UserBookingsNotFoundException(String message) {
        super(message);
    }
}
