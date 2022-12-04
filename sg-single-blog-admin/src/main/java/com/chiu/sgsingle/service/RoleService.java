package com.chiu.sgsingle.service;

import com.chiu.sgsingle.entity.RoleEntity;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:25 am
 */
public interface RoleService {

    RoleEntity findByCode(String role);
}
