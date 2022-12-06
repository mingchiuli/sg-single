package com.chiu.sgsingle.service;


import com.chiu.sgsingle.entity.RoleMenuEntity;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:25 am
 */
public interface RoleMenuService {

    void deleteByRoleId(Long roleId);

    List<Long> findMenuIdsByRoleId(Long id);

    void saveAll(List<RoleMenuEntity> roleMenus);

}
