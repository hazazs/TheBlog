package hu.hazazs.blog.service;

import hu.hazazs.blog.entity.Blogger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    @Value("${service.password.password-validator.pattern}")
    private String pattern;
    public Blogger changePassword(Blogger blogger, String password) {
        blogger.setPassword(encode(password));
        return blogger;
    }
    public static String decode(String password) {
        return new String(Base64.getDecoder().decode(password));
    }
    public static String encode(String password) {
        return new String(Base64.getEncoder().encode(password.getBytes()));
    }
    public String forgotPassword(String email, Blogger blogger) {
        if (!EmailService.emailValidator(email))
            return "email-invalid";
        if (blogger == null)
            return "email-unregistered";
        return "SUCCESS";
    }
    public boolean passwordValidator(String password) {
        return password.matches(pattern);
    }
    public List<String> resetPassword(String password1, Blogger blogger, String password2) {
        List<String> status = new ArrayList<>();
        if (!passwordValidator(password1))
            status.add("password-invalid");
        else if (password1.equals(decode(blogger.getPassword())))
            status.add("password-same");
        if (!password1.equals(password2))
            status.add("password-different");
        return status;
    }
}