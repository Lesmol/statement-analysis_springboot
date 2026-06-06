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

    /**
     * Handles bean validation failures and produces a 400 Bad Request ErrorResponse.
     *
     * @param e the MethodArgumentNotValidException containing validation errors
     * @return a ResponseEntity with status 400 and an ErrorResponse whose message is VALIDATION_FAILED and whose description is a comma-separated list of "field: message" entries for each field error
     */
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

    /**
     * Handles IO-related failures during request handling and maps them to an HTTP 500 response.
     *
     * @param e the IOException that occurred during file or I/O processing
     * @return a ResponseEntity with status 500 containing an ErrorResponse whose message indicates a file/IO error and whose description provides a fixed user-facing explanation
     */
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

    /**
     * Handles uncaught exceptions and returns a generic internal server error response.
     *
     * @param e the unhandled exception that triggered this handler
     * @return a ResponseEntity with HTTP 500 and an ErrorResponse containing a generic error message and the exception's message as the description
     */
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
