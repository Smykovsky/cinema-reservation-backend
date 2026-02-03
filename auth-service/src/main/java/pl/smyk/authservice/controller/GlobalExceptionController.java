package pl.smyk.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import pl.smyk.authservice.dto.ApiResponse;
import pl.smyk.authservice.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(NoHandlerFoundException ex) {
        ApiResponse<Object> response = ApiResponse.of("Nie ma takiej ścieżki API :(", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException e) {
        ApiResponse<Object> response = ApiResponse.of(e.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
