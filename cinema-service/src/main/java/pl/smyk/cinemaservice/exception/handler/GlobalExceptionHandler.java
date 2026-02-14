package pl.smyk.cinemaservice.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.smyk.cinemaservice.exception.*;

import pl.smyk.cinemaservice.exception.HallHasNoSeatsException; // Nowy import
import pl.smyk.common.dto.ErrorResponse;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CinemaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCinemaNotFoundException(CinemaNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CinemaAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCinemaAlreadyExistsException(CinemaAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(HallNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHallNotFoundException(HallNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HallAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleHallAlreadyExistsException(HallAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(HallHasNoSeatsException.class)
    public ResponseEntity<ErrorResponse> handleHallHasNoSeatsException(HallHasNoSeatsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ScreeningNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScreeningNotFoundException(ScreeningNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ScreeningAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleScreeningAlreadyExistsException(ScreeningAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotFoundException(SeatNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SeatAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSeatAlreadyExistsException(SeatAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AuthorizationDeniedException ex) {
        ErrorResponse error = new ErrorResponse("Nie posiadasz uprawnień do tego zasobu");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
