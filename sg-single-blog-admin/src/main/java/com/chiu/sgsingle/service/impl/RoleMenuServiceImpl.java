package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.entity.RoleMenuEntity;
import com.chiu.sgsingle.repository.RoleMenuRepository;
import com.chiu.sgsingle.service.RoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:26 am
 */
@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    RoleMenuRepository roleMenuRepository;

    public RoleMenuServiceImpl(RoleMenuRepository roleMenuRepository) {
        this.roleMenuRepository = roleMenuRepository;
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleMenuRepository.deleteByRoleId(roleId);
    }

    @Override
    public List<Long> findMenuIdsByRoleId(Long id) {
        return roleMenuRepository.findMenuIdsByRoleId(id);
    }

    @Override
    public void saveAll(List<RoleMenuEntity> roleMenus) {
        roleMenuRepository.saveAll(roleMenus);
    }
}
