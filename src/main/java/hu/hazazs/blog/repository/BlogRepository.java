package hu.hazazs.blog.repository;

import hu.hazazs.blog.entity.Blog;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends CrudRepository<Blog, Long> {
    @Query(value = "DELETE FROM BLOGS WHERE BLOGGER_ID = :id", nativeQuery = true)
    @Modifying
    @Transactional
    public void deleteBlogs(@Param("id") Long id);
    public List<Blog> findAllByOrderByPostedDesc();
    @Query(value = "SELECT * FROM BLOGS WHERE BLOGGER_ID = :id ORDER BY POSTED DESC", nativeQuery = true)
    public List<Blog> findByBloggerOrderByPostedDesc(@Param("id") Long id);
    @Query(value = "SELECT MAX(ID) + 1 FROM BLOGS", nativeQuery = true)
    public Long getNextId();
}