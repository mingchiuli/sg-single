package com.chiu.sgsingle.repository;

import com.chiu.sgsingle.entity.BlogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mingchiuli
 * @create 2022-11-27 1:30 am
 */
@Repository
public interface BlogRepository extends CrudRepository<BlogEntity, Long> {

    BlogEntity findByIdAndStatus(Long id, Integer status);

    @Query("UPDATE BlogEntity entity set entity.readCount = entity.readCount + 1 where entity.id = ?1")
    @Modifying
    @Transactional
    void setReadCount(Long id);

    @Query(value = "SELECT new BlogEntity (id, title, description, created, link) from BlogEntity")
    Page<BlogEntity> findAll(Pageable pageRequest);
}
