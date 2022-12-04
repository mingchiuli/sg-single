package com.chiu.sgsingle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author mingchiuli
 * @create 2022-12-03 11:36 pm
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BlogEntityDto {

    private Long id;

    private String username;

    private String title;

    private String description;

    private String content;

    private LocalDateTime created;

    private Integer status;

    private Long readCount;

    private Integer readRecent;
}
