package pl.smyk.movieservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import pl.smyk.movieservice.dto.ErrorResponse;
import pl.smyk.movieservice.exception.MovieAlreadyExistsException;
import pl.smyk.movieservice.exception.MovieNotFoundException;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(NoHandlerFoundException ex) {
        ErrorResponse error = new ErrorResponse("Nie ma takiej ścieżki API :(");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMovieNotFound(MovieNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MovieAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMovieAlreadyExists(MovieAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ===== Generic Exception Handler =====

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("Wystąpił nieoczekiwany błąd serwera");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AuthorizationDeniedException ex) {
        pl.smyk.common.dto.ErrorResponse error = new pl.smyk.common.dto.ErrorResponse("Nie posiadasz uprawnień do tego zasobu");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
