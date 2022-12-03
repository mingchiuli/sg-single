package com.chiu.sgsingle.mq.handler.impl;

import com.chiu.sgsingle.document.BlogDocument;
import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.mq.handler.BlogIndexHandler;
import com.chiu.sgsingle.repository.BlogRepository;
import com.chiu.sgsingle.search.BlogIndexEnum;
import com.chiu.sgsingle.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mingchiuli
 * @create 2022-12-03 4:50 pm
 */
@Component
public class UpdateBlogIndexHandler extends BlogIndexHandler {
    ObjectMapper objectMapper;
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public UpdateBlogIndexHandler(RedisTemplate<String, Object> redisTemplate, BlogRepository blogRepository, ObjectMapper objectMapper, ElasticsearchTemplate elasticsearchTemplate) {
        super(redisTemplate, blogRepository);
        this.objectMapper = objectMapper;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public boolean supports(BlogIndexEnum blogIndexEnum) {
        return BlogIndexEnum.UPDATE.equals(blogIndexEnum);
    }

    @Override
    protected void redisProcess(BlogEntity blog) {
        //不分年份的页数
        long count = blogRepository.getPageCount(blog.getCreated().toString());
        count++;
        long pageNo = count % Const.PAGE_SIZE == 0 ? count / Const.PAGE_SIZE : count / Const.PAGE_SIZE + 1;
        String sb = "::" + pageNo;
        String pageNoPrefix = Const.HOT_BLOGS + "::BlogController::listPage" + sb;

        //分年份的页数
        long countYear = blogRepository.getPageCountYear(blog.getCreated().toString(), blog.getCreated().getYear());
        countYear++;
        long pageYearNo = countYear % Const.PAGE_SIZE == 0 ? countYear / Const.PAGE_SIZE : countYear / Const.PAGE_SIZE + 1;
        String s = "::" + pageYearNo + "::" + blog.getCreated().getYear();
        String pageYearNoPrefix = Const.HOT_BLOGS + "::BlogController::listPageByYear" + s;

        //博客对象本身缓存
        StringBuilder builder = new StringBuilder();
        builder.append("::");
        builder.append(blog.getId());
        String contentKey = Const.HOT_BLOG + "::BlogServiceImpl::findByIdAndStatus" + builder;
        String statusKey = Const.BLOG_STATUS + "::BlogController::getBlogStatus" + builder;

        Set<String> keys = redisTemplate.keys(Const.HOT_BLOGS_PATTERN);
        if (keys == null) {
            keys = new HashSet<>();
        }

        keys.add(contentKey);
        keys.add(statusKey);
        keys.add(pageNoPrefix);
        keys.add(pageYearNoPrefix);
        redisTemplate.unlink(keys);
    }

    @Override
    protected void elasticSearchProcess(BlogEntity blog) {
        BlogDocument blogDocument = BlogDocument.builder().
                id(blog.getId()).
                userId(blog.getUserId()).
                title(blog.getTitle()).
                description(blog.getDescription()).
                content(blog.getContent()).
                status(blog.getStatus()).
                link(blog.getLink()).
                created(ZonedDateTime.of(blog.getCreated(), ZoneId.of("Asia/Shanghai"))).
                build();

        elasticsearchTemplate.save(blogDocument);
    }
}
