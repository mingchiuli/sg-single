package com.chiu.sgsingle.search;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author mingchiuli
 * @create 2021-12-13 10:46 AM
 */
@Data
@AllArgsConstructor
public class BlogSearchIndexMessage implements Serializable {
    private Long blogId;
    public BlogIndexEnum typeEnum;
}
