package pl.smyk.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pl.smyk.notificationservice.dto.EmailRequest;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService{
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendEmail(EmailRequest request) {
        try {
            Context context = new Context();
            context.setVariable("title", request.getTitle());
            context.setVariable("description", request.getDescription());

            String htmlContent = templateEngine.process("email-template", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(htmlContent, true);
            helper.setFrom("ksmyk.dev2000@gmail.com");

            mailSender.send(message);
            log.info("Email sent to: {}", request.getTo());

        } catch (MessagingException e) {
            log.error("Error sending email to: {}", request.getTo(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}