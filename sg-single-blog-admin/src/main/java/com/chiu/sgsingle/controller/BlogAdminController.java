package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.dto.BlogEntityDto;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.service.BlogService;
import com.chiu.sgsingle.vo.BlogEntityVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-01 9:28 pm
 */
@RestController
@RequestMapping(value = "/sys")
@Validated
public class BlogAdminController {

    BlogService blogService;

    public BlogAdminController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PreAuthorize("hasAnyRole(@highestRoleHolder.getRole(), @defaultRoleHolder.getRole())")
    @PostMapping("/blog/edit")
    public Result<Object> saveOrUpdate(@RequestBody @Validated BlogEntityVo blog) {
        blogService.saveOrUpdate(blog);
        return Result.success();
    }

    @PreAuthorize("hasAnyRole(@highestRoleHolder.getRole())")
    @PostMapping("/blog/delete")
    public Result<Object> deleteBlogs(@RequestBody List<Long> ids) {
        blogService.deleteBlogs(ids);
        return Result.success();
    }

    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    @GetMapping("/set/blog/token")
    public Result<Object> setBlogToken() {
        blogService.setBlogToken();
        return Result.success();
    }

    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    @GetMapping("/get/blog/token")
    public Result<String> getBlogToken() {
        String token = blogService.getBlogToken();
        return Result.success(token);
    }

    @PreAuthorize("hasAnyRole(@highestRoleHolder.getRole(), @defaultRoleHolder.getRole())")
    @GetMapping("/blogs/get")
    public Result<PageAdapter<BlogEntityDto>> getAllBlogs(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "5") Integer size) {
        PageAdapter<BlogEntityDto> page = blogService.getAllABlogs(currentPage, size);
        return Result.success(page);
    }

    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    @GetMapping("/search/blogs")
    public Result<PageAdapter<BlogEntityDto>> searchAllBlogs(@RequestParam String keyword, @RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "5") Integer size) {
        PageAdapter<BlogEntityDto> page = blogService.searchAllBlogs(keyword, currentPage, size);
        return Result.success(page);
    }
}
