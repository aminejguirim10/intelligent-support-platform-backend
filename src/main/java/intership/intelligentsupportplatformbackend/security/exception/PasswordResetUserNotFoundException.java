package intership.intelligentsupportplatformbackend.security.exception;

public class PasswordResetUserNotFoundException extends RuntimeException {
    public PasswordResetUserNotFoundException(String message) {
        super(message);
    }
}