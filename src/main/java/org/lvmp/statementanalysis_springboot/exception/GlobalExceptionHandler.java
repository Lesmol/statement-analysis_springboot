package org.lvmp.statementanalysis_springboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;

import java.io.IOException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String AN_ERROR_OCCURRED = "An error occurred with our services";
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String FILE_PROCESSING_FAILED = "File processing failed";

    private static final String FILE_PROCESSING_FAILED_DESCRIPTION = "An error occurred when processing your file. Please try uploading it again.";

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

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(HandlerMethodValidationException e) {
        log.error(e.getMessage(), e);

        String validationMessage = e.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(org.springframework.context.MessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Invalid file upload");

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .message("Validation Failed")
                        .description(validationMessage) // Contains your custom message
                        .build()
        );
    }

    @ExceptionHandler(AwsServiceException.class)
    public ErrorResponse handleAwsSeviceException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .message(AN_ERROR_OCCURRED)
                        .description(e.getMessage())
                        .build()
        );
    }
}
