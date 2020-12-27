package hu.hazazs.blog.repository;

import hu.hazazs.blog.entity.Blogger;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BloggerRepository extends CrudRepository<Blogger, Long> {
    @Query(value = "DELETE FROM BLOGGERS_ROLES WHERE BLOGGER_ID = :id", nativeQuery = true)
    @Modifying
    @Transactional
    public void deleteRoles(@Param("id") Long id);
    @Query(value = "SELECT * FROM BLOGGERS ORDER BY FULL_NAME", nativeQuery = true)
    public List<Blogger> findAllOrderByFullName();
    public Blogger findByEmail(String email);
    @Query(value = "SELECT * FROM BLOGGERS WHERE LOWER(FULL_NAME) LIKE %:search% ORDER BY FULL_NAME", nativeQuery = true)
    public List<Blogger> findBySearchOrderByFullName(@Param("search") String search);
    public Blogger findByUsername(String username);
    public Blogger findByVerificationCode(String code);
    @Query(value = "SELECT MAX(ID) + 1 FROM BLOGGERS", nativeQuery = true)
    public Long getNextId();
}