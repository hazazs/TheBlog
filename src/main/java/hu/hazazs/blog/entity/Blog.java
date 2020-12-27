package hu.hazazs.blog.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "BLOGS")
public class Blog implements Serializable {
    @Id
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date posted;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @ManyToOne
    private Blogger blogger;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getPosted() {
        return posted;
    }
    public void setPosted(Date posted) {
        this.posted = posted;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Blogger getBlogger() {
        return blogger;
    }
    public void setBlogger(Blogger blogger) {
        this.blogger = blogger;
    }
}