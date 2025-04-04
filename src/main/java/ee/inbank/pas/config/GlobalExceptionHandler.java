package ee.inbank.pas.config;

import ee.inbank.model.ErrorResponse;
import ee.inbank.pas.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(buildErrorResponse(ex.getMessage(), ex.getCode()));
    }

    private static ErrorResponse buildErrorResponse(String description, String code) {
        return ErrorResponse.builder()
            .description(description)
            .code(code)
            .build();
    }
}
