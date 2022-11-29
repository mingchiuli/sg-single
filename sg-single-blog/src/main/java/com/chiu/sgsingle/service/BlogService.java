package com.chiu.sgsingle.service;

import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.page.PageAdapter;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-11-27 2:12 pm
 */
public interface BlogService {
    BlogEntity findByIdAndStatus(Long id, Integer status);
    void setReadCount(Long id);

    BlogEntity findById(Long id);

    PageAdapter<BlogEntity> listPage(Integer currentPage);

    PageAdapter<BlogEntity> listPageByYear(Integer currentPage, Integer year);

    Integer getCountByYear(Integer year);

    BlogEntity getLockedBlog(Long blogId, String token);

    Integer getBlogStatus(Long blogId);

    List<Integer> searchYears();

    List<BlogEntity> findByStatus(int status);

    List<BlogEntity> findAll();

    Integer count();
}
