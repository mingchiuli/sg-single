package com.chiu.sgsingle.repository;

import com.chiu.sgsingle.entity.BlogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-11-27 1:30 am
 */
@Repository
public interface BlogRepository extends CrudRepository<BlogEntity, Long> {

    BlogEntity findByIdAndStatus(Long id, Integer status);

    @Query(value = "UPDATE BlogEntity entity SET entity.readCount = entity.readCount + 1 WHERE entity.id = ?1")
    @Modifying
    @Transactional
    void setReadCount(Long id);

    @Query(value = "SELECT new BlogEntity (id, title, description, created, link) FROM BlogEntity")
    Page<BlogEntity> findAll(Pageable pageRequest);

    @Query(value = "SELECT new BlogEntity (blog.id, blog.title, blog.description, blog.created, blog.link) FROM BlogEntity blog WHERE blog.created BETWEEN :start AND :end")
    Page<BlogEntity> findAllByYear(Pageable pageRequest, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT count(1) from m_blog where created between ?1 and ?2", nativeQuery = true)
    Integer countByYear(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT status from m_blog where id = ?1", nativeQuery = true)
    Integer getBlogStatus(Long blogId);

    @Query(value = "SELECT distinct year(created) from m_blog order by year(created)", nativeQuery = true)
    List<Integer> searchYears();

    List<BlogEntity> findAllByStatus(int status);

    @Query(value = "SELECT count(1) from m_blog", nativeQuery = true)
    Integer findCount();
}
