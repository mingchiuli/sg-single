package com.chiu.sgsingle.service;

import java.util.Map;

/**
 * @author mingchiuli
 * @create 2022-11-27 8:27 pm
 */
public interface CaptchaService {

    Map<String, String> createCaptcha();
}
