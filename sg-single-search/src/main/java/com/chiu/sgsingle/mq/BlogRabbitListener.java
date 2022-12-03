package com.chiu.sgsingle.mq;

import com.chiu.sgsingle.config.RabbitConfig;
import com.chiu.sgsingle.mq.handler.BlogIndexHandler;
import com.chiu.sgsingle.search.BlogSearchIndexMessage;
import com.chiu.sgsingle.utils.SpringUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * @author mingchiuli
 * @create 2021-12-13 11:38 AM
 */
@Slf4j
@Component
public class BlogRabbitListener {
    private static class CacheHandlers {
        private static final Map<String, BlogIndexHandler> cacheHandlers = SpringUtils.getHandlers(BlogIndexHandler.class);
    }

    @RabbitListener(queues = RabbitConfig.ES_QUEUE)
    public void handler(BlogSearchIndexMessage message, Channel channel, Message msg) {
        for (BlogIndexHandler handler : CacheHandlers.cacheHandlers.values()) {
            if (handler.supports(message.typeEnum)) {
                handler.handle(message, channel, msg);
                break;
            }
        }

    }
}
