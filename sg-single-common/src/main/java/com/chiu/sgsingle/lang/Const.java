package com.chiu.sgsingle.lang;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author mingchiuli
 * @create 2021-12-14 11:58 AM
 */
@Component
public class Const {
    public final static String CAPTCHA_KEY = "captcha:";
    public final static String TOKEN = "token";

    public final static String READ_RECENT = "blogReadRecent:";

    public final static String HOT_BLOGS_PATTERN = "hot_blogs*";

    //url相关，不相关上传路径
    public final static String UPLOAD_IMG_PATH = "/upload/img/";

    public static final String CO_PREFIX = "co_blogId:";

    public static final String CO_NUM_PREFIX = "co_num:";

    public static final String QUERY_ALL_DELETED = ":blog:*";

    public static final String QUERY_DELETED = ":blog:";

    public static final String READ_TOKEN = "read_token";

    public static final String HOT_BLOGS = "hot_blogs";
    public static final String HOT_BLOG = "hot_blog";

    public static final String BLOG_STATUS = "blog_status";

    public static final String CONSUME_MONITOR = "consume:";

    public static final String BLOOM_FILTER_BLOG = "bloom_filter_blog";

    public static final String BLOOM_FILTER_PAGE = "bloom_filter_page";
    public static final String BLOOM_FILTER_YEAR_PAGE = "bloom_filter_page_";
    public final static String YEARS = "years";
    public final static String BLOOM_FILTER_YEARS = "bloom_filter_years";

    public static Integer PAGE_SIZE;

    public static Integer WEB_SIZE;

    @Value("${blog.blog-page-size}")
    public void setPageSize(Integer pageSize) {
        PAGE_SIZE = pageSize;
    }

    @Value("${blog.web-page-size}")
    public void setWebSize(Integer webSize) {
        WEB_SIZE = webSize;
    }
}
