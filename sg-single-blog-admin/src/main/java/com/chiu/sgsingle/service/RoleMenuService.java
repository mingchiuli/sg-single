package com.chiu.sgsingle.service;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:25 am
 */
public interface RoleMenuService {

    List<Long> getNavMenuIds(String role);
}
