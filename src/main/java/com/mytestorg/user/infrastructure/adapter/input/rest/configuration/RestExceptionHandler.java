package com.mytestorg.user.infrastructure.adapter.input.rest.configuration;

import com.mytestorg.user.infrastructure.adapter.input.rest.dto.response.ErrorResponse;
import com.mytestorg.user.infrastructure.exception.UserAlreadyExistsException;
import com.mytestorg.user.infrastructure.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler{
    private static final String GENERAL_ERROR_MESSAGE = "Service has an internal error. Please try later";


    /**
     * Handles {@link UserNotFoundException} thrown when a user is not found.
     *
     * @param exception the thrown exception
     * @return {@link ResponseEntity} with a {@link ErrorResponse} and HTTP status 404 (Not Found)
     */
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException exception) {
        log.info("Got a request for a non existing resource", exception);
        return buildErrorResponseEntity(HttpStatus.NOT_FOUND, ErrorResponse.ErrorCode.REQ_RESOURCE_NOT_FOUND,
                "User can't be fetched/updated/deleted as no user found with given id", null);
    }

    /**
     * Handles {@link UserAlreadyExistsException} thrown when trying to create a user with an existing email or identifier.
     *
     * @param exception the thrown exception
     * @return {@link ResponseEntity} with a {@link ErrorResponse} and HTTP status 409 (Conflict)
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException exception) {
        log.info("Got a request to update an existing user which doesn't exist", exception);
        return buildErrorResponseEntity(HttpStatus.CONFLICT, ErrorResponse.ErrorCode.USER_ALREADY_EXISTS,
                exception.getMessage(),null);
    }

    /**
     * Handles all uncaught exceptions not handled by more specific handlers.
     *
     * @param exception the exception to handle
     * @return {@link ResponseEntity} with {@link ErrorResponse} and HTTP status 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        log.error("Error while processing a rest request", exception);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponse.ErrorCode.SERVER_ERROR,
                GENERAL_ERROR_MESSAGE, null);
    }

    /**
     * Handles validation errors for request data, such as invalid method arguments or constraint violations.
     *
     * @param exception the {@link MethodArgumentNotValidException} thrown when validation on an argument annotated with {@code @Valid} fails
     * @return {@link ResponseEntity} with {@link ErrorResponse} and HTTP status 400 (Bad Request), including details about each invalid field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(MethodArgumentNotValidException exception) {
        log.warn("Validation error on request: {}", exception.getMessage(), exception);

        Map<String, String> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));

        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, ErrorResponse.ErrorCode.REQ_INVALID,
                "Validation failed for one or more fields.", fieldErrors);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponseEntity(HttpStatus httpStatus,
                                                                   ErrorResponse.ErrorCode errorCode, String exceptionMessage, Map<String, String> fieldErrors) {
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(errorCode, exceptionMessage, fieldErrors));
    }
}
