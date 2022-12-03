package com.chiu.sgsingle.service;

import com.chiu.sgsingle.dto.BlogEntityDto;
import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.vo.BlogEntityVo;

import java.time.LocalDateTime;
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
    List<BlogEntity> findAll();
    Integer count();
    void saveOrUpdate(BlogEntityVo blog);
    void deleteBlogs(List<Long> ids);
    void setBlogToken();
    String getBlogToken();
    PageAdapter<BlogEntityDto> getAllABlogs(Integer currentPage, Integer size);
    PageAdapter<BlogEntityDto> searchAllBlogs(String keyword, Integer currentPage, Integer size);
}
