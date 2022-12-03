package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


/**
 * @author mingchiuli
 * @create 2022-11-27 6:32 pm
 */
@RestController
public class CaptchaController {
    CaptchaService captchaService;
    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/captcha")
    public Result<Map<String, String>> createCaptcha() {
        Map<String, String> captcha = captchaService.createCaptcha();
        return Result.success(captcha);
    }
}
