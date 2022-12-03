package com.chiu.sgsingle.mq.handler;

import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.repository.BlogRepository;
import com.chiu.sgsingle.search.BlogIndexEnum;
import com.chiu.sgsingle.search.BlogSearchIndexMessage;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.LocalDateTime;
import java.util.Optional;


public abstract class BlogIndexHandler {

    protected RedisTemplate<String, Object> redisTemplate;
    protected BlogRepository blogRepository;
    public BlogIndexHandler(RedisTemplate<String, Object> redisTemplate, BlogRepository blogRepository) {
        this.redisTemplate = redisTemplate;
        this.blogRepository = blogRepository;
    }

    public abstract boolean supports(BlogIndexEnum blogIndexEnum);
    protected abstract void redisProcess(BlogEntity blog);
    protected abstract void elasticSearchProcess(BlogEntity blog);

    @SneakyThrows
    public void handle(BlogSearchIndexMessage message, Channel channel, Message msg) {
        String createUUID = msg.getMessageProperties().getHeader(PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(Const.CONSUME_MONITOR + createUUID))) {
            Long blogId = message.getBlogId();
            Integer year = message.getYear();
            Optional<BlogEntity> blog = blogRepository.findById(blogId);
            BlogEntity blogEntity = blog.orElse(BlogEntity.
                    builder().
                    id(blogId).
                    created(LocalDateTime.of(year, 1,1,1 ,1 ,1, 1)).
                    build());

            redisProcess(blogEntity);
            elasticSearchProcess(blogEntity);
            long deliveryTagCreate = msg.getMessageProperties().getDeliveryTag();
            //手动签收消息
            //false代表不是批量签收模式
            channel.basicAck(deliveryTagCreate, false);
            redisTemplate.delete(Const.CONSUME_MONITOR + createUUID);
        } else {
            long deliveryTag = msg.getMessageProperties().getDeliveryTag();
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
