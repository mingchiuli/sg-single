package com.chiu.sgsingle;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SgSingleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgSingleApplication.class, args);
    }

}
