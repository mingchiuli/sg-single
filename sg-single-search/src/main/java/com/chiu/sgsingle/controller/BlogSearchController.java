package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.service.BlogSearchService;
import com.chiu.sgsingle.valid.ListValue;
import com.chiu.sgsingle.vo.BlogDocumentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mingchiuli
 * @create 2022-11-30 8:48 pm
 */
@RestController
@Validated
public class BlogSearchController {

    BlogSearchService blogSearchService;

    public BlogSearchController(BlogSearchService blogSearchService) {
        this.blogSearchService = blogSearchService;
    }

    @GetMapping("/search/{flag}/{currentPage}")
    public Result<PageAdapter<BlogDocumentVo>> selectBlogsByES(@PathVariable Integer currentPage, @PathVariable @ListValue(values = {0, 1}, message = "必须提交0或1") Integer flag ,  @RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "keyword") String keyword) {
        PageAdapter<BlogDocumentVo> page = blogSearchService.selectBlogsByES(currentPage, keyword, flag, year);
        return Result.success(page);
    }

}
