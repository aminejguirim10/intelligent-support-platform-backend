package intership.intelligentsupportplatformbackend.security.service;

import intership.intelligentsupportplatformbackend.security.exception.PasswordResetEmailSendException;
import intership.intelligentsupportplatformbackend.security.service.email.EmailTemplate;
import intership.intelligentsupportplatformbackend.security.service.email.TemplateEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PasswordResetEmailService {

    private final TemplateEmailService templateEmailService;

    @Value("${application.security.password-reset.token-expiration-minutes:15}")
    private long resetTokenExpirationMinutes;

    @Value("${application.mail.brand-name:Intelligent Support Platform}")
    private String brandName;

    public void sendPasswordResetEmail(String recipientEmail, String recipientName, String resetLink) {
        String safeName = (recipientName == null || recipientName.isBlank()) ? "User" : recipientName;

        Map<String, Object> templateVariables = Map.of(
                "recipientName", safeName,
                "resetLink", resetLink,
                "expirationMinutes", resetTokenExpirationMinutes,
                "brandName", brandName
        );

        try {
            templateEmailService.sendHtmlEmail(
                    recipientEmail,
                    EmailTemplate.PASSWORD_RESET,
                    templateVariables,
                    buildPlainTextFallback(safeName, resetLink)
            );
        } catch (MessagingException | RuntimeException ex) {
            throw new PasswordResetEmailSendException(
                    "Failed to send password reset email",
                    ex
            );
        }
    }

    private String buildPlainTextFallback(String recipientName, String resetLink) {
        return "Hello " + recipientName + ",\n\n"
                + "We received a request to reset your password.\n"
                + "Use the link below to create a new password:\n"
                + resetLink + "\n\n"
                + "This link will expire in " + resetTokenExpirationMinutes + " minutes.\n"
                + "If you did not request a password reset, you can safely ignore this email.\n\n"
                + "Best regards,\n"
                + "The " + brandName + " Team";
    }
}