package intership.intelligentsupportplatformbackend.security.exception;

public class PasswordResetPasswordMismatchException extends RuntimeException {
    public PasswordResetPasswordMismatchException(String message) {
        super(message);
    }
}
