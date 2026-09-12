package com.campuscrate.exception;

import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFound(CategoryNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<ApiError> handleDuplicateCategory(DuplicateCategoryException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ApiError> handleLocationNotFound(LocationNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateLocationException.class)
    public ResponseEntity<ApiError> handleDuplicateLocation(DuplicateLocationException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiError> handleItemNotFound(ItemNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ItemReferenceNotFoundException.class)
    public ResponseEntity<ApiError> handleItemReferenceNotFound(ItemReferenceNotFoundException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ApiError> handleClaimNotFound(ClaimNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ClaimReferenceNotFoundException.class)
    public ResponseEntity<ApiError> handleClaimReferenceNotFound(ClaimReferenceNotFoundException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DuplicateClaimException.class)
    public ResponseEntity<ApiError> handleDuplicateClaim(DuplicateClaimException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<ApiError> handleAdminNotFound(AdminNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateAdminException.class)
    public ResponseEntity<ApiError> handleDuplicateAdmin(DuplicateAdminException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(StatusHistoryReferenceException.class)
    public ResponseEntity<ApiError> handleStatusHistoryReference(StatusHistoryReferenceException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MarketplacePostNotFoundException.class)
    public ResponseEntity<ApiError> handleMarketplacePostNotFound(MarketplacePostNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(MarketplaceReferenceNotFoundException.class)
    public ResponseEntity<ApiError> handleMarketplaceReferenceNotFound(
            MarketplaceReferenceNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(MarketplaceInvalidRequestException.class)
    public ResponseEntity<ApiError> handleMarketplaceInvalidRequest(
            MarketplaceInvalidRequestException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MarketplaceForbiddenException.class)
    public ResponseEntity<ApiError> handleMarketplaceForbidden(MarketplaceForbiddenException exception) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(MarketplaceConflictException.class)
    public ResponseEntity<ApiError> handleMarketplaceConflict(MarketplaceConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateStudentIdException.class)
    public ResponseEntity<ApiError> handleDuplicateStudentId(DuplicateStudentIdException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({ InvalidRequestException.class, MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ApiError> handleInvalidRequest(Exception exception) {
        String message = exception.getMessage();
        if (exception instanceof MethodArgumentNotValidException validationException) {
            message = validationException.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else if (exception instanceof MethodArgumentTypeMismatchException) {
            message = "ID must be a number";
        }
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException exception) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ApiError> handleImageUpload(ImageUploadException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ShuttleNotFoundException.class)
    public ResponseEntity<ApiError> handleShuttleNotFound(ShuttleNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ShuttleInvalidRequestException.class)
    public ResponseEntity<ApiError> handleShuttleInvalidRequest(ShuttleInvalidRequestException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ShuttleConflictException.class)
    public ResponseEntity<ApiError> handleShuttleConflict(ShuttleConflictException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ShuttleForbiddenException.class)
    public ResponseEntity<ApiError> handleShuttleForbidden(ShuttleForbiddenException exception) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDatabaseError(DataAccessException exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Database operation failed");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException exception) {
        return error(HttpStatus.FORBIDDEN, "You are not allowed to perform this action");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException exception) {
        return error(HttpStatus.UNAUTHORIZED, "Authentication is required");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(HttpMessageNotReadableException exception) {
        return error(HttpStatus.BAD_REQUEST, "Malformed JSON request body");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), message));
    }
}
