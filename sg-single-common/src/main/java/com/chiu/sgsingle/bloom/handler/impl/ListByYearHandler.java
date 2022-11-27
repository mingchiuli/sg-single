package com.chiu.sgsingle.bloom.handler.impl;


import com.chiu.sgsingle.bloom.handler.BloomHandler;
import com.chiu.sgsingle.exception.NotFoundException;
import com.chiu.sgsingle.lang.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ListByYearHandler implements BloomHandler {
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ListByYearHandler.class);
    }

    @Override
    public void handle(Object[] args) {
        Integer currentPage = (Integer) args[0];
        Integer yearMark = (Integer) args[1];
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().getBit(Const.BLOOM_FILTER_PAGE + yearMark, currentPage))) {
            throw new NotFoundException("没有" + yearMark + "年份" + currentPage + "页面！");
        }
    }
}
