package hu.hazazs.blog.service;

import hu.hazazs.blog.entity.Blogger;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {
    public static String generateVerificationCode() {
        char[] code = new char[16];
        for (int i = 0; i < code.length; i++)
            code[i] = (char) (new Random().nextInt(26) + 'a');
        return new String(code);
    }
    public String verification(Blogger blogger) {
        if (blogger == null)
            return "verification-failed";
        if (blogger.getVerified())
            return "already-verified";
        blogger.setVerified(true);
        return "SUCCESS";
    }
}