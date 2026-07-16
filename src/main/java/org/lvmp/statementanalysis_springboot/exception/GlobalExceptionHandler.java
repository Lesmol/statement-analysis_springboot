package org.lvmp.statementanalysis_springboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;

import java.io.IOException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String AN_ERROR_OCCURRED = "An error occurred with our services";
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String AUTHENTICATION_FAILED = "Unauthorised";
    private static final String FILE_PROCESSING_FAILED = "File processing failed";

    private static final String FILE_PROCESSING_FAILED_DESCRIPTION = "An error occurred when processing your file. Please try uploading it again.";

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthenticationException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .message(AUTHENTICATION_FAILED)
                        .description(e.getMessage())
                        .build()
        );
    }

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

    @ExceptionHandler({IOException.class, SdkClientException.class, SdkServiceException.class})
    public ResponseEntity<ErrorResponse> handleIOException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .message(FILE_PROCESSING_FAILED)
                        .description(FILE_PROCESSING_FAILED_DESCRIPTION)
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
