package pl.smyk.cinemaservice.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.smyk.cinemaservice.exception.*;

import pl.smyk.cinemaservice.dto.ErrorResponse;
import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CinemaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCinemaNotFoundException(CinemaNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CinemaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCinemaAlreadyExistsException(CinemaAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(HallNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHallNotFoundException(HallNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(HallAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleHallAlreadyExistsException(HallAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ScreeningNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScreeningNotFoundException(ScreeningNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ScreeningAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleScreeningAlreadyExistsException(ScreeningAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotFoundException(SeatNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SeatAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyExistsException(SeatAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(message, LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, status);
    }
}
