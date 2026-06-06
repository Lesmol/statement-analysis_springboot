package org.lvmp.statementanalysis_springboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String AN_ERROR_OCCURRED = "An error occurred with our services";
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String IO_EXCEPTION = "File processing failed";

    private static final String IO_EXCEPTION_DESCRIPTION = "An error occurred when processing your file. Please try uploading it again.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);

        String validationDetails = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .message(VALIDATION_FAILED)
                        .description(validationDetails)
                        .build()
        );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .message(IO_EXCEPTION)
                        .description(IO_EXCEPTION_DESCRIPTION)
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .message(AN_ERROR_OCCURRED)
                        .description(e.getMessage())
                        .build()
        );
    }
}
