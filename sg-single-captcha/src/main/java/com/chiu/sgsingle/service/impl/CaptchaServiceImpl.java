package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.service.CaptchaService;
import com.google.code.kaptcha.Producer;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author mingchiuli
 * @create 2022-11-27 8:28 pm
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    Producer producer;
    StringRedisTemplate redisTemplate;
    public CaptchaServiceImpl(Producer producer, StringRedisTemplate redisTemplate) {
        this.producer = producer;
        this.redisTemplate = redisTemplate;
    }

    @SneakyThrows
    @Override
    public Map<String, String> createCaptcha() {
        String key = UUID.randomUUID().toString();
        String code = producer.createText();

        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        Base64.Encoder encoder = Base64.getEncoder();
        String str = "data:image/jpeg;base64,";

        String base64Img = str + encoder.encodeToString(outputStream.toByteArray());

        redisTemplate.opsForValue().set(Const.CAPTCHA_KEY + key, code, 12000, TimeUnit.SECONDS);

        HashMap<String, String> map = new HashMap<>();

        map.put(Const.TOKEN, key);
        map.put("captchaImg", base64Img);
        return map;
    }
}
