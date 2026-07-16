package intership.intelligentsupportplatformbackend.security.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateEmailService {

    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine;

    @Value("${application.mail.from:${spring.mail.username}}")
    private String fromEmail;

    public void sendHtmlEmail(
            String recipientEmail,
            EmailTemplate template,
            Map<String, Object> templateVariables,
            String plainTextFallback
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setFrom(fromEmail);
        helper.setTo(recipientEmail);
        helper.setSubject(template.getSubject());

        Context context = new Context(Locale.FRENCH);
        context.setVariables(templateVariables);

        String htmlBody = templateEngine.process(template.getTemplatePath(), context);

        if (plainTextFallback == null || plainTextFallback.isBlank()) {
            helper.setText(htmlBody, true);
        } else {
            helper.setText(plainTextFallback, htmlBody);
        }

        mailSender.send(mimeMessage);
    }
}
