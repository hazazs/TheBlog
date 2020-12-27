package hu.hazazs.blog.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "ROLES")
public class Role implements Serializable {
    @Id
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @ManyToMany(mappedBy = "roles")
    private Set<Blogger> bloggers = new HashSet<>();
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<Blogger> getBloggers() {
        return bloggers;
    }
    public void setBloggers(Set<Blogger> bloggers) {
        this.bloggers = bloggers;
    }
}