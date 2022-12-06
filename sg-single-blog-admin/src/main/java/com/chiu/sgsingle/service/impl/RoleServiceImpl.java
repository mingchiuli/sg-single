package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.entity.RoleEntity;
import com.chiu.sgsingle.entity.RoleMenuEntity;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.repository.RoleRepository;
import com.chiu.sgsingle.service.RoleMenuService;
import com.chiu.sgsingle.service.RoleService;
import com.chiu.sgsingle.vo.RoleEntityVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:26 am
 */
@Service
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;

    RoleMenuService roleMenuService;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMenuService roleMenuService) {
        this.roleRepository = roleRepository;
        this.roleMenuService = roleMenuService;
    }

    @Override
    public RoleEntity info(Long id) {
        Optional<RoleEntity> roleEntity = roleRepository.findById(id);
        return roleEntity.orElseThrow();
    }

    @Override
    public PageAdapter<RoleEntity> listPage(Integer currentPage, Integer pageSize) {
        Pageable pageRequest = PageRequest.of(currentPage - 1,
                pageSize,
                Sort.by("created").ascending());
        Page<RoleEntity> page = roleRepository.findAll(pageRequest);
        return new PageAdapter<>(page);
    }

    @Override
    public void saveOrUpdate(RoleEntityVo roleVo) {
        var ref = new Object() {
            RoleEntity roleEntity;
        };

        Optional.ofNullable(roleVo.getId()).ifPresentOrElse((id) -> {
            ref.roleEntity = roleRepository.findById(id).orElseThrow();
            ref.roleEntity.setUpdated(LocalDateTime.now());
        }, () -> {
            ref.roleEntity = new RoleEntity();
            ref.roleEntity.setCreated(LocalDateTime.now());
            ref.roleEntity.setUpdated(LocalDateTime.now());
        });

        BeanUtils.copyProperties(roleVo, ref.roleEntity);
        roleRepository.save(ref.roleEntity);
    }

    @Override
    public List<Long> getNavMenuIds(String role) {
        RoleEntity roleEntity = roleRepository.findByCode(role).orElseThrow();
        Long id = roleEntity.getId();
        return roleMenuService.findMenuIdsByRoleId(id);
    }


    @Override
    @Transactional
    public void delete(List<Long> ids) {
        ids.forEach(id -> {
            roleRepository.deleteById(id);
            roleMenuService.deleteByRoleId(id);
        });
    }


    @Override
    @Transactional
    public List<Long> perm(Long roleId, List<Long> menuIds) {
        List<RoleMenuEntity> roleMenus = new ArrayList<>();
        menuIds.forEach(menuId -> {
            RoleMenuEntity roleMenu = new RoleMenuEntity();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);
            roleMenus.add(roleMenu);
        });

        roleMenuService.deleteByRoleId(roleId);
        roleMenuService.saveAll(roleMenus);
        return menuIds;
    }
}
