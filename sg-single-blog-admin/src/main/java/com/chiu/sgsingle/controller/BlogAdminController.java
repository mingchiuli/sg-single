package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.service.BlogService;
import com.chiu.sgsingle.vo.BlogEntityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mingchiuli
 * @create 2022-12-01 9:28 pm
 */
@RestController
public class BlogAdminController {

    BlogService blogService;

    @Autowired
    public void setBlogService(BlogService blogService) {
        this.blogService = blogService;
    }

    @PreAuthorize("hasAnyRole(@highestRoleHolder.getRole(), @defaultRoleHolder.getRole())")
    @PostMapping("/blog/edit")
    public Result<Object> saveOrUpdate(@RequestBody @Validated BlogEntityVo blog) {
        blogService.saveOrUpdate(blog);
        return Result.success();
    }
}
