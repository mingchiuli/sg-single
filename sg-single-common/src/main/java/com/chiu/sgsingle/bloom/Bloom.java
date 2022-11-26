package com.chiu.sgsingle.bloom;


import java.lang.annotation.*;

/**
 * @author mingchiuli
 * @create 2022-06-07 11:00 AM
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bloom {
    Class<?> name();
}
