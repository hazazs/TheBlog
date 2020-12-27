package hu.hazazs.blog.service;

import hu.hazazs.blog.entity.Blogger;
import hu.hazazs.blog.entity.Role;
import java.util.Collection;
import java.util.HashSet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    public String login(Blogger blogger, String password) {
        if (blogger == null || !password.equals(PasswordService.decode(blogger.getPassword())))
            return "bad-credentials";
        if (!blogger.getVerified())
            return "account-unverified";
        Collection<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : blogger.getRoles())
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(blogger.getUsername(), blogger.getPassword(), authorities));
        return "SUCCESS";
    }
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}