package com.chiu.sgsingle.bloom.handler.impl;


import com.chiu.sgsingle.bloom.handler.BloomHandler;
import com.chiu.sgsingle.exception.NotFoundException;
import com.chiu.sgsingle.lang.Const;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DetailBloomHandler implements BloomHandler {

    StringRedisTemplate redisTemplate;

    public DetailBloomHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(DetailBloomHandler.class);
    }

    @Override
    public void handle(Object[] args) {
        Long blogId = (Long) args[0];
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().getBit(Const.BLOOM_FILTER_BLOG, blogId))) {
            throw new NotFoundException("没有"+ blogId + "号博客！");
        }
    }
}
