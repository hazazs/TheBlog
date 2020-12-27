package hu.hazazs.blog.service;

import hu.hazazs.blog.entity.Blogger;
import hu.hazazs.blog.repository.BloggerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BloggerService {
    private BloggerRepository bloggerRepository;
    @Autowired
    public void setBloggerRepository(BloggerRepository bloggerRepository) {
        this.bloggerRepository = bloggerRepository;
    }
    public void deleteById(Long id) {
        bloggerRepository.deleteById(id);
    }
    public void deleteRoles(Long id) {
        bloggerRepository.deleteRoles(id);
    }
    public List<Blogger> findAllOrderByFullName() {
        return bloggerRepository.findAllOrderByFullName();
    }
    public Blogger findByEmail(String email) {
        return bloggerRepository.findByEmail(email);
    }
    public Blogger findById(Long id) {
        return bloggerRepository.findById(id).get();
    }
    public List<Blogger> findBySearchOrderByFullName(String search) {
        return bloggerRepository.findBySearchOrderByFullName(search);
    }
    public Blogger findByUsername(String username) {
        return bloggerRepository.findByUsername(username);
    }
    public Blogger findByVerificationCode(String code) {
        return bloggerRepository.findByVerificationCode(code);
    }
    public Long getNextId() {
        return bloggerRepository.getNextId();
    }
    public void save(Blogger blogger) {
        bloggerRepository.save(blogger);
    }
}