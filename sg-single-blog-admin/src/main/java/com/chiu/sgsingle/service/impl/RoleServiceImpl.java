package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.entity.RoleEntity;
import com.chiu.sgsingle.repository.RoleRepository;
import com.chiu.sgsingle.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:26 am
 */
@Service
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleEntity findByCode(String role) {
        return roleRepository.findByCode(role).orElseThrow();
    }
}
