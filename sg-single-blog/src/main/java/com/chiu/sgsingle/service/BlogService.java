package com.chiu.sgsingle.service;

import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.page.PageAdapter;

/**
 * @author mingchiuli
 * @create 2022-11-27 2:12 pm
 */
public interface BlogService {
    BlogEntity findByIdAndStatus(Long id, Integer status);
    void setReadCount(Long id);

    BlogEntity findById(Long id);

    PageAdapter<BlogEntity> listPage(Integer currentPage);
}
