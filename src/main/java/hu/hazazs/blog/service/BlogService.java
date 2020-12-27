package hu.hazazs.blog.service;

import hu.hazazs.blog.entity.Blog;
import hu.hazazs.blog.entity.Blogger;
import hu.hazazs.blog.repository.BlogRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogService {
    private BlogRepository blogRepository;
    @Autowired
    public void setBlogRepository(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }
    public void deleteBlogs(Long id) {
        blogRepository.deleteBlogs(id);
    }
    public void deleteById(Long id) {
        blogRepository.deleteById(id);
    }
    public Blog edit(Blog blog, String title, String content) {
        blog.setTitle(title);
        blog.setPosted(new Date());
        blog.setContent(content);
        return blog;
    }
    public Blog fill(Blog blog, Blogger blogger) {
        blog.setId(getNextId() == null ? 1 : getNextId());
        blog.setPosted(new Date());
        blog.setBlogger(blogger);
        return blog;
    }
    public List<Blog> findAllByOrderByPostedDesc() {
        return blogRepository.findAllByOrderByPostedDesc();
    }
    public List<Blog> findByBloggerOrderByPostedDesc(Long id) {
        return blogRepository.findByBloggerOrderByPostedDesc(id);
    }
    public List<Blog> findById(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isPresent())
            return Stream.of(blog.get()).collect(Collectors.toList());
        return new ArrayList<>();
    }
    public Long getNextId() {
        return blogRepository.getNextId();
    }
    public void save(Blog blog) {
        blogRepository.save(blog);
    }
}