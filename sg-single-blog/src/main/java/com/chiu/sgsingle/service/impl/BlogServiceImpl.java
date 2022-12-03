package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.search.BlogIndexEnum;
import com.chiu.sgsingle.cache.Cache;
import com.chiu.sgsingle.config.RabbitConfig;
import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.entity.UserEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.repository.BlogRepository;
import com.chiu.sgsingle.repository.UserRepository;
import com.chiu.sgsingle.search.BlogSearchIndexMessage;
import com.chiu.sgsingle.service.BlogService;
import com.chiu.sgsingle.vo.BlogEntityVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author mingchiuli
 * @create 2022-11-27 2:10 pm
 */
@Service
@Slf4j
public class BlogServiceImpl implements BlogService {
    BlogRepository blogRepository;
    RedisTemplate<String, Object> redisTemplate;
    UserRepository userRepository;
    RabbitTemplate rabbitTemplate;

    public BlogServiceImpl(BlogRepository blogRepository, RedisTemplate<String, Object> redisTemplate, UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.blogRepository = blogRepository;
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Cache(prefix = Const.HOT_BLOG)
    public BlogEntity findByIdAndStatus(Long id, Integer status) {
        return blogRepository.findByIdAndStatus(id, status);
    }

    @Async(value = "readCountThreadPoolExecutor")
    @Override
    @SuppressWarnings("unchecked")
    public void setReadCount(Long id) {
        blogRepository.setReadCount(id);
        try {
            redisTemplate.execute(new SessionCallback<>() {
                @Override
                public List<Object> execute(@NonNull RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForValue().setIfAbsent(Const.READ_RECENT + id, 0, 7, TimeUnit.DAYS);
                    operations.opsForValue().increment(Const.READ_RECENT + id, 1);
                    return operations.exec();
                }
            });
        } catch (NestedRuntimeException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public BlogEntity findById(Long id) {
        return blogRepository.findById(id).orElseThrow();
    }

    @Override
    public PageAdapter<BlogEntity> listPage(Integer currentPage) {
        Pageable pageRequest = PageRequest.of(currentPage - 1, Const.PAGE_SIZE, Sort.by("created").descending());
        Page<BlogEntity> page = blogRepository.findAll(pageRequest);
        return new PageAdapter<>(page);
    }

    @Override
    public PageAdapter<BlogEntity> listPageByYear(Integer currentPage, Integer year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1 , 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, 12, 31 , 23, 59, 59);
        Pageable pageRequest = PageRequest.of(currentPage - 1, Const.PAGE_SIZE, Sort.by("created").descending());
        Page<BlogEntity> page = blogRepository.findAllByYear(pageRequest, start, end);
        return new PageAdapter<>(page);
    }

    @Override
    public Integer getCountByYear(Integer year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1 , 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, 12, 31 , 23, 59, 59);
        return blogRepository.countByYear(start, end);
    }

    @Override
    public BlogEntity getLockedBlog(Long blogId, String token) {
        token = token.trim();
        String password = (String) redisTemplate.opsForValue().get(Const.READ_TOKEN);
        if (StringUtils.hasLength(token) && StringUtils.hasLength(password)) {
            if (token.equals(password)) {
                return blogRepository.findByIdAndStatus(blogId, 1);
            }
        }
        return null;
    }

    @Override
    public Integer getBlogStatus(Long blogId) {
        return blogRepository.getBlogStatus(blogId);
    }

    @Override
    public List<Integer> searchYears() {
        return blogRepository.searchYears();
    }


    @Override
    public List<BlogEntity> findAll() {
        List<BlogEntity> entities = new ArrayList<>();
        for (BlogEntity blogEntity : blogRepository.findAll()) {
            entities.add(blogEntity);
        }
        return entities;
    }

    @Override
    public Integer count() {
        return blogRepository.findCount();
    }

    @Override
    public void saveOrUpdate(BlogEntityVo blog) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username);
        BlogEntity blogEntity;
        BlogIndexEnum type;
        if (blog.getId() == null) {
            blogEntity = new BlogEntity();
            blogEntity.setCreated(LocalDateTime.now());
            blogEntity.setUserId(user.getId());
            blogEntity.setReadCount(0L);
            type = BlogIndexEnum.CREATE;
        } else {
            Optional<BlogEntity> optionalBlog = blogRepository.findById(blog.getId());
            blogEntity = optionalBlog.orElseThrow();
            Assert.isTrue(blogEntity.getUserId().equals(user.getId()), "只能编辑自己的文章!");
            type = BlogIndexEnum.UPDATE;
        }
        BeanUtils.copyProperties(blog, blogEntity);
        blogEntity = blogRepository.save(blogEntity);

        //通知消息给mq,更新并删除缓存
        CorrelationData correlationData = new CorrelationData();
        //防止重复消费
        redisTemplate.opsForValue().set(Const.CONSUME_MONITOR + correlationData.getId(),
                        type.name() + "_" + blogEntity.getId(),
                        30,
                        TimeUnit.SECONDS);

        rabbitTemplate.convertAndSend(
                RabbitConfig.ES_EXCHANGE,
                RabbitConfig.ES_BINDING_KEY,
                new BlogSearchIndexMessage(blogEntity.getId(), type, blogEntity.getCreated().getYear()),
                correlationData);
    }


    @Override
    public void deleteBlogs(List<Long> ids) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        ids.forEach(id -> {
            Optional<BlogEntity> optionalBlog = blogRepository.findById(id);
            BlogEntity blogEntity = optionalBlog.orElseThrow();
            blogRepository.delete(blogEntity);
            redisTemplate.opsForValue().set( username + Const.QUERY_DELETED + id,
                    blogEntity,
                    7,
                    TimeUnit.DAYS);

            CorrelationData correlationData = new CorrelationData();
            //防止重复消费
            redisTemplate.opsForValue().set(Const.CONSUME_MONITOR + correlationData.getId(),
                    BlogIndexEnum.REMOVE.name() + "_" +  id,
                    30,
                    TimeUnit.SECONDS);

            rabbitTemplate.convertAndSend(
                    RabbitConfig.ES_EXCHANGE,
                    RabbitConfig.ES_BINDING_KEY,
                    new BlogSearchIndexMessage(id, BlogIndexEnum.REMOVE, blogEntity.getCreated().getYear()), correlationData);
        });
    }
}
