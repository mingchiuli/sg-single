package com.chiu.sgsingle.mq.handler.impl;

import com.chiu.sgsingle.document.BlogDocument;
import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.mq.handler.BlogIndexHandler;
import com.chiu.sgsingle.repository.BlogRepository;
import com.chiu.sgsingle.search.BlogIndexEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CreateBlogIndexHandler extends BlogIndexHandler {
    ObjectMapper objectMapper;
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public CreateBlogIndexHandler(RedisTemplate<String, Object> redisTemplate, BlogRepository blogRepository, ObjectMapper objectMapper, ElasticsearchTemplate elasticsearchTemplate) {
        super(redisTemplate, blogRepository);
        this.objectMapper = objectMapper;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }


    @Override
    public boolean supports(BlogIndexEnum blogIndexEnum) {
        return BlogIndexEnum.CREATE.equals(blogIndexEnum);
    }


    @Override
    protected void redisProcess(BlogEntity blog) {
        Set<String> keys = redisTemplate.keys(Const.HOT_BLOGS_PATTERN);

        if (keys == null) {
            keys = new HashSet<>();
        }
        keys.add(Const.BLOOM_FILTER_YEAR_PAGE + blog.getCreated().getYear());
        redisTemplate.unlink(keys);

        redisTemplate.opsForValue().setBit(Const.BLOOM_FILTER_BLOG, blog.getId(), true);
        redisTemplate.opsForValue().set(Const.READ_RECENT + blog.getId(), 0, 7, TimeUnit.DAYS);

        //年份过滤bloom更新
        int year = blog.getCreated().getYear();
        redisTemplate.opsForValue().setBit(Const.BLOOM_FILTER_YEARS, year, true);
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
