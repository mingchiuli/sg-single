package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.entity.RoleEntity;
import com.chiu.sgsingle.entity.RoleMenuEntity;
import com.chiu.sgsingle.repository.RoleMenuRepository;
import com.chiu.sgsingle.service.RoleMenuService;
import com.chiu.sgsingle.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:26 am
 */
@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    RoleService roleService;

    RoleMenuRepository roleMenuRepository;

    public RoleMenuServiceImpl(RoleService roleService, RoleMenuRepository roleMenuRepository) {
        this.roleService = roleService;
        this.roleMenuRepository = roleMenuRepository;
    }

    @Override
    public List<Long> getNavMenuIds(String role) {
        RoleEntity roleEntity = roleService.findByCode(role);
        Long id = roleEntity.getId();
        return roleMenuRepository.findMenuIdsByRoleId(id);
    }
}
