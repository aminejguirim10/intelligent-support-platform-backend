package intership.intelligentsupportplatformbackend.security.service.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {
    PASSWORD_RESET("emails/password-reset", "Password Reset Request"),
    WELCOME("emails/welcome", "Welcome to the Intelligent Support Platform");

    private final String templatePath;
    private final String subject;
}