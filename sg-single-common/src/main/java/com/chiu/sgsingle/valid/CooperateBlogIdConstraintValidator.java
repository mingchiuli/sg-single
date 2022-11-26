package com.chiu.sgsingle.valid;

import com.chiu.sgsingle.lang.Const;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class CooperateBlogIdConstraintValidator implements ConstraintValidator<CooperateBlogId, Long> {

    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void initialize(CooperateBlogId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long blogId, ConstraintValidatorContext constraintValidatorContext) {
        return redisTemplate.opsForHash().size(Const.CO_PREFIX + blogId) < 3;
    }
}
