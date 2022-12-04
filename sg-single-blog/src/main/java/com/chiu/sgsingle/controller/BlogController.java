package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.bloom.Bloom;
import com.chiu.sgsingle.bloom.handler.impl.*;
import com.chiu.sgsingle.cache.Cache;
import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.service.BlogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-11-26 5:30 pm
 */
@RestController
public class BlogController {

    BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/blog/{id}")
    @Bloom(handler = DetailBloomHandler.class)
    public Result<BlogEntity> getBlogDetail(@PathVariable(name = "id") Long id) {
        BlogEntity blog = blogService.findByIdAndStatus(id, 0);
        blogService.setReadCount(id);
        return Result.success(blog);
    }

    @GetMapping("/blog/authorized/{id}")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<BlogEntity> getLockedBlogDetail(@PathVariable(name = "id") Long id) {
        BlogEntity blog = blogService.findById(id);
        blogService.setReadCount(id);
        return Result.success(blog);
    }

    @GetMapping("/blogs/{currentPage}")
    @Cache(prefix = Const.HOT_BLOGS)
    @Bloom(handler = ListBloomHandler.class)
    public Result<PageAdapter<BlogEntity>> listPage(@PathVariable(name = "currentPage") Integer currentPage) {
        PageAdapter<BlogEntity> pageData = blogService.listPage(currentPage);
        return Result.success(pageData);
    }

    @GetMapping("/blogs/year/{year}/{currentPage}")
    @Cache(prefix = Const.HOT_BLOGS)
    @Bloom(handler = ListByYearBloomHandler.class)
    public Result<PageAdapter<BlogEntity>> listPageByYear(@PathVariable(name = "currentPage") Integer currentPage, @PathVariable(name = "year") Integer year) {
        PageAdapter<BlogEntity> pageData = blogService.listPageByYear(currentPage, year);
        return Result.success(pageData);
    }

    @GetMapping("/count/year/{year}")
    @Cache(prefix = Const.HOT_BLOGS)
    @Bloom(handler = CountByYearBloomHandler.class)
    public Result<Integer> getCountByYear(@PathVariable(name = "year") Integer year) {
        Integer count = blogService.getCountByYear(year);
        return Result.success(count);
    }

    @GetMapping("blog/token/{blogId}/{token}")
    public Result<BlogEntity> getLockedBlog(@PathVariable Long blogId, @PathVariable String token) {
        BlogEntity blog = blogService.getLockedBlog(blogId, token);
        return Result.success(blog);
    }

    @Bloom(handler = BlogStatusBloomHandler.class)
    @GetMapping("/blog/status/{blogId}")
    @Cache(prefix = Const.BLOG_STATUS)
    public Result<Integer> getBlogStatus(@PathVariable Long blogId) {
        Integer status = blogService.getBlogStatus(blogId);
        return Result.success(status);
    }

    @GetMapping("/search/years")
    @Cache(prefix = Const.YEARS)
    public Result<List<Integer>> searchYears() {
        List<Integer> years = blogService.searchYears();
        return Result.success(years);
    }

}
