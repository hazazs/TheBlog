package hu.hazazs.blog.service;

import hu.hazazs.blog.entity.Blogger;
import hu.hazazs.blog.entity.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "service.signup.fullname-validator")
public class SignupService {
    @Value("${service.signup.username-validator.pattern}")
    private String pattern;
    private Map<String, String> patterns;
    public void setPatterns(Map<String, String> patterns) {
        this.patterns = patterns;
    }
    public Blogger fill(Blogger blogger, Long id, String password, Role role) {
        blogger.setId(id);
        blogger.setPassword(PasswordService.encode(password));
        blogger.getRoles().add(role);
        blogger.setVerificationCode(VerificationService.generateVerificationCode());
        blogger.setVerified(false);
        blogger.setAvatar(blogger.getGender() + ".png");
        return blogger;
    }
    public boolean fullNameValidator(String fullName) {
        return fullName.matches(patterns.values().stream().collect(Collectors.joining("|")));
    }
    public List<String> signupStatus(Blogger blogger, Blogger bloggerByUsername, Blogger bloggerByEmail, boolean passwordValid, String password1, String password2) {
        List<String> status = new ArrayList<>();
        if (!blogger.getUsername().matches(pattern))
            status.add("username-invalid");
        if (bloggerByUsername != null)
            status.add("username-reserved");
        if (!EmailService.emailValidator(blogger.getEmail()))
            status.add("email-invalid");
        else if (bloggerByEmail != null)
            status.add("email-reserved");
        if (!passwordValid)
            status.add("password-invalid");
        if (!password1.equals(password2))
            status.add("password-different");
        if (!fullNameValidator(blogger.getFullName()))
            status.add("fullname_invalid");
        return status;
    }
}