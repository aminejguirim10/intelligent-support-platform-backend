package intership.intelligentsupportplatformbackend.security.exception;

import intership.intelligentsupportplatformbackend.dto.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

/*
 * Global exception handler for security-related exceptions.
 * Provides consistent error responses for authentication and authorization failures.
 */
@RestControllerAdvice
public class SecurityExceptionHandler {

    /*
     * Handle bad credentials exception (invalid login).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBadCredentialsException(
            BadCredentialsException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    /*
     * Handle generic authentication exceptions.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleAuthenticationException(
            AuthenticationException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    /*
     * Handle access denied exception (insufficient permissions).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleAccessDeniedException(
            AccessDeniedException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    /*
     * Handle expired JWT token.
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleExpiredJwtException(
            ExpiredJwtException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    /*
     * Handle malformed JWT token.
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleMalformedJwtException(
            MalformedJwtException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    /*
     * Handle invalid JWT signature.
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleSignatureException(
            SignatureException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    /*
     * Handle IllegalArgumentException (e.g., duplicate email).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(PasswordResetUserNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handlePasswordResetUserNotFoundException(
            PasswordResetUserNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleInvalidPasswordResetTokenException(
            InvalidPasswordResetTokenException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handlePasswordResetTokenExpiredException(
            PasswordResetTokenExpiredException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(PasswordResetPasswordMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handlePasswordResetPasswordMismatchException(
            PasswordResetPasswordMismatchException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(PasswordResetEmailSendException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handlePasswordResetEmailSendException(
            PasswordResetEmailSendException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

}
