package com.chiu.sgsingle.vo;

import com.chiu.sgsingle.valid.ListValue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author mingchiuli
 * @create 2022-12-01 9:26 pm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogEntityVo {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "摘要不能为空")
    private String description;

    @NotBlank(message = "内容不能为空")
    private String content;

    @ListValue(values = {0,1})
    private Integer status;
    private String link;
}
