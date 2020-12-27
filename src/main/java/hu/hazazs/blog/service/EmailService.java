package hu.hazazs.blog.service;

import hu.hazazs.blog.BlogApplication;
import hu.hazazs.blog.entity.Blogger;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "service")
public class EmailService {
    @Value("${spring.mail.username}")
    private String admin;
    private Map<String, Map<String, String>> email;
    private JavaMailSender javaMailSender;
    public void setEmail(Map<String, Map<String, String>> email) {
        this.email = email;
    }
    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public static boolean emailValidator(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
    public boolean sendEmail(String recipient, String subject, String content, String type) {
        try {
            MimeMessagePreparator message = mime -> {
                mime.setFrom(admin);
                mime.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                mime.setSubject(subject);
                mime.setContent(content, type);
            };
            javaMailSender.send(message);
        } catch(Exception exception) {
            BlogApplication.getLogger().error(exception.getMessage());
            return false;
          }
        return true;
    }
    public boolean sendEmailToAdmin(String subject, String email, String content) {
        return sendEmail(this.email.get("help").get("recipient"), subject + " (" + email + ")", content, "text/plain; charset=UTF-8");
    }
    public boolean sendEmailToBlogger(Blogger blogger, String type) {
        try {
            if (type.equals("verification") || type.equals("reset-password"))
                return sendEmail(blogger.getEmail(), email.get(type).get("subject"), FileUtils.readFileToString(new File(email.get("templates").get("verification-and-reset-password")), StandardCharsets.UTF_8)
                    .replace("[blogger]", blogger.getUsername())
                    .replace("[instruction]", email.get(type).get("instruction"))
                    .replace("[url]", email.get(type).get("URL") + blogger.getVerificationCode())
                    .replace("[button]", email.get(type).get("button")), "text/html; charset=UTF-8");
            return sendEmail(blogger.getEmail(), email.get(type).get("subject"), FileUtils.readFileToString(new File(email.get("templates").get("delete-account")), StandardCharsets.UTF_8)
                    .replace("[blogger]", blogger.getUsername()), "text/html; charset=UTF-8");
        } catch (IOException exception) {
            BlogApplication.getLogger().error(exception.getMessage());
            return false;
          }
    }
}