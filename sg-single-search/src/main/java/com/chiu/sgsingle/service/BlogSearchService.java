package com.chiu.sgsingle.service;

import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.vo.BlogDocumentVo;

/**
 * @author mingchiuli
 * @create 2022-11-30 8:52 pm
 */
public interface BlogSearchService {

    PageAdapter<BlogDocumentVo> selectBlogsByES(Integer currentPage, String keyword, Integer flag, Integer year);

}
