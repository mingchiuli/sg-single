package com.chiu.sgsingle.bloom.handler.impl;


import com.chiu.sgsingle.bloom.handler.BloomHandler;
import com.chiu.sgsingle.exception.NotFoundException;
import com.chiu.sgsingle.lang.Const;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CountByYearBloomHandler implements BloomHandler {

    RedisTemplate<String, Object> redisTemplate;

    public CountByYearBloomHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CountByYearBloomHandler.class);
    }

    @Override
    public void handle(Object[] args) {
        Integer year = (Integer) args[0];
        if (Boolean.FALSE.equals(redisTemplate.opsForValue().getBit(Const.BLOOM_FILTER_YEARS, year))) {
            throw new NotFoundException("没有" + year + "年份！");
        }
    }
}
