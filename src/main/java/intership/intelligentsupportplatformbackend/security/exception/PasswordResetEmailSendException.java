package intership.intelligentsupportplatformbackend.security.exception;

public class PasswordResetEmailSendException extends RuntimeException {
    public PasswordResetEmailSendException(String message) {
        super(message);
    }

    public PasswordResetEmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}