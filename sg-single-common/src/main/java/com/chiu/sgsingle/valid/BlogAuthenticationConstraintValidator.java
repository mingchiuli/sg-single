package com.chiu.sgsingle.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class BlogAuthenticationConstraintValidator implements ConstraintValidator<BlogAuthentication, Long> {

//    BlogService blogService;
//
//    @Autowired
//    public void setBlogService(BlogService blogService) {
//        this.blogService = blogService;
//    }

    @Override
    public void initialize(BlogAuthentication constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long blogId, ConstraintValidatorContext constraintValidatorContext) {
        return false;
//        return blogService.getOne(new QueryWrapper<BlogEntity>().select("status").eq("id", blogId)).getStatus() != 1;
    }
}
