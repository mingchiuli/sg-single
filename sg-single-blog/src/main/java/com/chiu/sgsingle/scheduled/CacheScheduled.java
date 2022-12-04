package com.chiu.sgsingle.scheduled;

import com.chiu.sgsingle.entity.BlogEntity;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.service.BlogService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mingchiuli
 * @create 2022-11-29 10:52 pm
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
public class CacheScheduled {

    ThreadPoolExecutor executor;

    BlogService blogService;

    RedisTemplate<String, Object> redisTemplate;

    public CacheScheduled(@Qualifier("scheduledThreadPoolExecutor") ThreadPoolExecutor executor, BlogService blogService, RedisTemplate<String, Object> redisTemplate) {
        this.executor = executor;
        this.blogService = blogService;
        this.redisTemplate = redisTemplate;
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 0/2 * * ?")
    public void configureTask() {
        long startMillis = System.currentTimeMillis();
        List<Integer> years = blogService.searchYears();
        CompletableFuture<Void> var1 = CompletableFuture.runAsync(() -> {
            //getBlogDetail和getBlogStatus接口
            List<BlogEntity> allBlogs = blogService.findAll();
            List<BlogEntity> blogs = allBlogs.stream().
                    filter(blog -> blog.getStatus() == 0).
                    toList();

            blogs.forEach(blog -> {

                StringBuilder builder = new StringBuilder();
                builder.append("::").append(blog.getId());
                String contentPrefix = Const.HOT_BLOG + "::BlogServiceImpl::getBlogDetail" + builder;
                String statusPrefix = Const.BLOG_STATUS + "::BlogController::getBlogStatus" + builder;

                redisTemplate.opsForValue().set(contentPrefix, blog, ThreadLocalRandom.current().nextInt(120) + 1, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(statusPrefix, Result.success(blog.getStatus()), ThreadLocalRandom.current().nextInt(120) + 1, TimeUnit.MINUTES);
            });

            //bloomFilter
            allBlogs.forEach(blog -> redisTemplate.opsForValue().setBit(Const.BLOOM_FILTER_BLOG, blog.getId(), true));

        }, executor);



        CompletableFuture<Void> var2 = CompletableFuture.runAsync(() -> {
            //list接口
            Integer count = blogService.count();
            int totalPage = count % Const.PAGE_SIZE == 0 ? count / Const.PAGE_SIZE : count / Const.PAGE_SIZE + 1;

            for (int no = 1; no <= totalPage; no++) {
                PageAdapter<BlogEntity> page = blogService.listPage(no);
                String pagesPrefix = Const.HOT_BLOGS + "::BlogController::listPage" + "::" + no;
                redisTemplate.opsForValue().set(pagesPrefix, Result.success(page), ThreadLocalRandom.current().nextInt(120) + 1, TimeUnit.MINUTES);
                //bloomFilter
                redisTemplate.opsForValue().setBit(Const.BLOOM_FILTER_PAGE, no, true);
            }
        }, executor);


        CompletableFuture<Void> var3 = CompletableFuture.runAsync(() -> {
            //getCountByYear接口
            years.forEach(year -> {
                Integer countYear = blogService.getCountByYear(year);
                String yearCountPrefix = Const.HOT_BLOGS + "::BlogController::getCountByYear" + "::" + year;
                redisTemplate.opsForValue().set(yearCountPrefix, Result.success(countYear), ThreadLocalRandom.current().nextInt(120) + 1, TimeUnit.MINUTES);
            });
        }, executor);


        CompletableFuture<Void> var4 = CompletableFuture.runAsync(() -> {
            //listByYear接口
            for (Integer year : years) {
                //当前年份的总页数
                Integer count = blogService.getCountByYear(year);
                int totalPage = count % Const.PAGE_SIZE == 0 ? count / Const.PAGE_SIZE : count / Const.PAGE_SIZE + 1;

                for (int no = 1; no <= totalPage; no++) {
                    //每一页的缓存
                    PageAdapter<BlogEntity> page = blogService.listPageByYear(no, year);
                    String yearListPrefix = Const.HOT_BLOGS + "::BlogController::listPageByYear" + "::" + no + "::" + year;;
                    redisTemplate.opsForValue().set(yearListPrefix, Result.success(page), ThreadLocalRandom.current().nextInt(120) + 1, TimeUnit.MINUTES);
                    //bloom过滤器
                    redisTemplate.opsForValue().setBit(Const.BLOOM_FILTER_YEAR_PAGE + year, no, true);
                }
            }
        }, executor);


        //searchYears和getCountByYear
        CompletableFuture<Void> var5 = CompletableFuture.runAsync(() -> {
            String yearKey = Const.YEARS + "::BlogController::searchYears";
            redisTemplate.opsForValue().set(yearKey, Result.success(years), ThreadLocalRandom.current().nextInt(120) + 1, TimeUnit.MINUTES);
            //getCountByYear的bloom
            years.forEach(year -> redisTemplate.opsForValue().setBit(Const.BLOOM_FILTER_YEARS, year, true));
        }, executor);

        CompletableFuture.allOf(var1, var2, var3, var4, var5).get();
        long endMillis = System.currentTimeMillis();

        log.info("定时任务执行用时{}毫秒", endMillis - startMillis);
    }

}
