package com.chiu.sgsingle.service.impl;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.chiu.sgsingle.document.BlogDocument;
import com.chiu.sgsingle.dto.BlogEntityDto;
import com.chiu.sgsingle.search.BlogIndexEnum;
import com.chiu.sgsingle.cache.Cache;
import com.chiu.sgsingle.config.RabbitConfig;
import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.entity.UserEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.repository.BlogRepository;
import com.chiu.sgsingle.search.BlogSearchIndexMessage;
import com.chiu.sgsingle.service.BlogService;
import com.chiu.sgsingle.service.UserService;
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
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.*;
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

    UserService userService;

    RabbitTemplate rabbitTemplate;

    ElasticsearchTemplate elasticsearchTemplate;

    public BlogServiceImpl(BlogRepository blogRepository, RedisTemplate<String, Object> redisTemplate, UserService userService, RabbitTemplate rabbitTemplate, ElasticsearchTemplate elasticsearchTemplate) {
        this.blogRepository = blogRepository;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Cache(prefix = Const.HOT_BLOG)
    public BlogEntity findByIdAndStatus(Long id, Integer status) {
        return blogRepository.findByIdAndStatus(id, status).orElseThrow();
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
        Pageable pageRequest = PageRequest.of(currentPage - 1,
                Const.PAGE_SIZE,
                Sort.by("created").descending());
        Page<BlogEntity> page = blogRepository.findAll(pageRequest);
        return new PageAdapter<>(page);
    }

    @Override
    public PageAdapter<BlogEntity> listPageByYear(Integer currentPage, Integer year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1 , 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, 12, 31 , 23, 59, 59);
        Pageable pageRequest = PageRequest.of(currentPage - 1,
                Const.PAGE_SIZE,
                Sort.by("created").descending());
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
                return blogRepository.findByIdAndStatus(blogId, 1).orElseThrow();
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
        Optional<UserEntity> user = userService.findByUsername(username);
        BlogEntity blogEntity;
        BlogIndexEnum type;
        if (blog.getId() == null) {
            blogEntity = new BlogEntity();
            blogEntity.setCreated(LocalDateTime.now());
            blogEntity.setUserId(user.orElseThrow().getId());
            blogEntity.setReadCount(0L);
            type = BlogIndexEnum.CREATE;
        } else {
            Optional<BlogEntity> optionalBlog = blogRepository.findById(blog.getId());
            blogEntity = optionalBlog.orElseThrow();
            Assert.isTrue(blogEntity.getUserId().equals(user.orElseThrow().getId()), "只能编辑自己的文章!");
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

    @Override
    public void setBlogToken() {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(Const.READ_TOKEN, token, 24, TimeUnit.HOURS);
    }

    @Override
    public String getBlogToken() {
        String token = (String) redisTemplate.opsForValue().get(Const.READ_TOKEN);
        if (token == null) {
            token = "阅读密钥目前没有设置";
        }
        return token;
    }

    @Override
    public PageAdapter<BlogEntityDto> getAllABlogs(Integer currentPage, Integer size) {
        Pageable pageRequest = PageRequest.of(currentPage - 1, size, Sort.by("created").descending());
        Page<BlogEntity> page = blogRepository.findAllAdmin(pageRequest);
        ArrayList<BlogEntityDto> entities = new ArrayList<>();

        page.getContent().forEach(blogEntity -> {
            BlogEntityDto entityDto = new BlogEntityDto();
            BeanUtils.copyProperties(blogEntity, entityDto);
            Integer readNum = (Integer) redisTemplate.opsForValue().get(Const.READ_RECENT + blogEntity.getId());
            Optional<UserEntity> userEntity = userService.findUsernameById(blogEntity.getUserId());
            entityDto.setUsername(userEntity.orElse(UserEntity.builder().username("anonymous").build()).getUsername());
            entityDto.setReadRecent(Objects.requireNonNullElse(readNum, 0));
            entities.add(entityDto);
        });

        return PageAdapter.<BlogEntityDto>builder().
                content(entities).
                last(page.isLast()).
                first(page.isFirst()).
                pageNumber(page.getNumber()).
                totalPages(page.getTotalPages()).
                pageSize(page.getSize()).
                totalElements(page.getTotalElements()).
                empty(page.isEmpty()).
                build();
    }

    @Override
    public PageAdapter<BlogEntityDto> searchAllBlogs(String keyword, Integer currentPage, Integer size) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query ->
                        query.multiMatch(multiQuery -> multiQuery.
                                fields(Arrays.asList("title", "description", "content")).query(keyword)))
                .withPageable(PageRequest.of(currentPage - 1, size))
                .withSort(sortQuery -> sortQuery.
                        field(fieldQuery -> fieldQuery.
                                field("created").order(SortOrder.Desc))).build();

        SearchHits<BlogDocument> search = elasticsearchTemplate.search(nativeQuery, BlogDocument.class);

        List<BlogEntityDto> entities = new ArrayList<>();
        search.getSearchHits().forEach(hit -> {
            BlogDocument content = hit.getContent();
            BlogEntityDto entityDto = new BlogEntityDto();
            BeanUtils.copyProperties(content, entityDto);
            Integer readNum = (Integer) redisTemplate.opsForValue().get(Const.READ_RECENT + content.getId());
            Optional<UserEntity> userEntity = userService.findUsernameById(content.getUserId());
            entityDto.setUsername(userEntity.orElse(UserEntity.builder().username("anonymous").build()).getUsername());
            entityDto.setReadRecent(Objects.requireNonNullElse(readNum, 0));
            entities.add(entityDto);
        });

        return PageAdapter.<BlogEntityDto>builder().
                totalPages((int) (search.getTotalHits() % size == 0 ? search.getTotalHits() / size : (search.getTotalHits() / size + 1))).
                totalElements(search.getTotalHits()).
                pageNumber(currentPage).
                pageSize(size).
                empty(search.isEmpty()).
                first(currentPage == 1).
                last(currentPage == (search.getTotalHits() % size == 0 ? search.getTotalHits() / size : search.getTotalHits() / size + 1)).
                content(entities).
                build();
    }
}
