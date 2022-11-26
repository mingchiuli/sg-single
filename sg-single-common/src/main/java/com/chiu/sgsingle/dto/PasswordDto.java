package com.chiu.sgsingle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author mingchiuli
 * @create 2021-12-14 2:08 PM
 */
@Data
public class PasswordDto {
    @NotBlank(message = "昵称不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
