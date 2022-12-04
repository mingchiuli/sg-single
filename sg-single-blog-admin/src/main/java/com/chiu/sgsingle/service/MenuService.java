package com.chiu.sgsingle.service;

import com.chiu.sgsingle.vo.MenuEntityVo;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:25 am
 */
public interface MenuService {

    List<MenuEntityVo> getCurrentUserNav(String username);
}
