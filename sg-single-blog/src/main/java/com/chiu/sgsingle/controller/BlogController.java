package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.bloom.Bloom;
import com.chiu.sgsingle.bloom.handler.impl.ListBloomHandler;
import com.chiu.sgsingle.bloom.handler.impl.PublicDetailHandler;
import com.chiu.sgsingle.cache.Cache;
import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mingchiuli
 * @create 2022-11-26 5:30 pm
 */
@RestController
public class BlogController {

    BlogService blogService;

    @Autowired
    public void setBlogService(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/blog/{id}")
    @Bloom(handler = PublicDetailHandler.class)
    @Cache(prefix = Const.HOT_BLOG)
    public Result<BlogEntity> getBlogDetail(@PathVariable(name = "id") Long id) {
        BlogEntity blog = blogService.findByIdAndStatus(id, 0);
        blogService.setReadCount(id);
        return Result.success(blog);
    }

    @GetMapping("/blogAuthorized/{id}")
    @PreAuthorize("hasRole(@highestRoleHolder.getHighestRole())")
    public Result<BlogEntity> getLockedBlogDetail(@PathVariable(name = "id") Long id) {
        BlogEntity blog = blogService.findById(id);
        blogService.setReadCount(id);
        return Result.success(blog);
    }

    @GetMapping("/blogs/{currentPage}")
    @Cache(prefix = Const.HOT_BLOGS)//缓存页面信息
    @Bloom(handler = ListBloomHandler.class)
    public Result<PageAdapter<BlogEntity>> listPage(@PathVariable(name = "currentPage") Integer currentPage) {
        PageAdapter<BlogEntity> pageData = blogService.listPage(currentPage);
        return Result.success(pageData);
    }

}
