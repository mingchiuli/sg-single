package com.chiu.sgsingle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author mingchiuli
 * @create 2021-11-06 6:24 PM
 */
@Configuration(proxyBeanMethods = false)
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 文件上传处理，URL映射到本地磁盘路径
     */
    @Value("${blog.add-resource-handler}")
    private String addResourceHandler;

    @Value("${blog.add-resource-locations}")
    private String addResourceLocations;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //环境
        registry.addResourceHandler(addResourceHandler).addResourceLocations(addResourceLocations);
    }

    /**
     * 解决跨域问题，经过测试，websocket和Spring Security的配置不兼容，采用原始配置，此时响应头无法添加Authorization，
     * 于是放到数据体中
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowCredentials(true)
                .maxAge(3600);
//                .allowedHeaders("*");
    }
}
