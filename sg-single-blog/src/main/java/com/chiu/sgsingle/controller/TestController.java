package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.lang.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mingchiuli
 * @create 2022-11-26 5:30 pm
 */
@RestController
public class TestController {

    @GetMapping("/")
    public Result<String> result() {
        return Result.success("izwddabdiuba");
    }
}
