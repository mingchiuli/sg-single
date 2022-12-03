package com.chiu.sgsingle.bloom.handler.impl;


import com.chiu.sgsingle.bloom.handler.BloomHandler;
import com.chiu.sgsingle.exception.NotFoundException;
import com.chiu.sgsingle.lang.Const;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ListByYearBloomHandler implements BloomHandler {
    RedisTemplate<String, Object> redisTemplate;

    public ListByYearBloomHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ListByYearBloomHandler.class);
    }

    @Override
    public void handle(Object[] args) {
        Integer currentPage = (Integer) args[0];
        Integer yearMark = (Integer) args[1];
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().getBit(Const.BLOOM_FILTER_YEAR_PAGE + yearMark, currentPage))) {
            throw new NotFoundException("没有" + yearMark + "年份" + currentPage + "页面！");
        }
    }
}
