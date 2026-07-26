package org.lvmp.statementanalysis_springboot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.services.rdsdata.model.AccessDeniedException;
import software.amazon.awssdk.services.rdsdata.model.BadRequestException;
import software.amazon.awssdk.services.rdsdata.model.DatabaseResumingException;
import software.amazon.awssdk.services.rdsdata.model.DatabaseUnavailableException;
import software.amazon.awssdk.services.rdsdata.model.ForbiddenException;
import software.amazon.awssdk.services.rdsdata.model.RdsDataException;
import software.amazon.awssdk.services.rdsdata.model.ServiceUnavailableErrorException;
import software.amazon.awssdk.services.rdsdata.model.StatementTimeoutException;

import java.io.IOException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String AN_ERROR_OCCURRED = "An error occurred with our services";
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String AUTHENTICATION_FAILED = "Unauthorised";
    private static final String FILE_PROCESSING_FAILED = "File processing failed";
    private static final String DATABASE_ERROR = "A database error occurred";

    private static final String FILE_PROCESSING_FAILED_DESCRIPTION = "An error occurred when processing your file. Please try uploading it again.";
    private static final String DATABASE_REQUEST_INVALID_DESCRIPTION = "There was a problem with your request. Please check your input and try again.";
    private static final String DATABASE_PERMISSION_DESCRIPTION = "You do not have permission to perform this action.";
    private static final String DATABASE_UNAVAILABLE_DESCRIPTION = "Our database is temporarily unavailable. Please try again shortly.";
    private static final String DATABASE_TIMEOUT_DESCRIPTION = "Your request took too long to process. Please try again.";
    private static final String DATABASE_ERROR_DESCRIPTION = "An error occurred while accessing the database. Please try again later.";

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

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .message(DATABASE_ERROR)
                        .description(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleRdsBadRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .message(DATABASE_ERROR)
                        .description(DATABASE_REQUEST_INVALID_DESCRIPTION)
                        .build()
        );
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleRdsPermissionException(RdsDataException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.builder()
                        .message(DATABASE_ERROR)
                        .description(DATABASE_PERMISSION_DESCRIPTION)
                        .build()
        );
    }

    @ExceptionHandler({ServiceUnavailableErrorException.class, DatabaseUnavailableException.class, DatabaseResumingException.class})
    public ResponseEntity<ErrorResponse> handleRdsUnavailableException(RdsDataException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                ErrorResponse.builder()
                        .message(DATABASE_ERROR)
                        .description(DATABASE_UNAVAILABLE_DESCRIPTION)
                        .build()
        );
    }

    @ExceptionHandler(StatementTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleRdsTimeoutException(StatementTimeoutException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(
                ErrorResponse.builder()
                        .message(DATABASE_ERROR)
                        .description(DATABASE_TIMEOUT_DESCRIPTION)
                        .build()
        );
    }

    @ExceptionHandler(RdsDataException.class)
    public ResponseEntity<ErrorResponse> handleRdsDataException(RdsDataException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.internalServerError().body(
                ErrorResponse.builder()
                        .message(DATABASE_ERROR)
                        .description(DATABASE_ERROR_DESCRIPTION)
                        .build()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        log.error(e.getMessage(), e);

        return ResponseEntity.status(e.getStatusCode()).body(
                ErrorResponse.builder()
                        .message(e.getReason())
                        .description(e.getReason())
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
