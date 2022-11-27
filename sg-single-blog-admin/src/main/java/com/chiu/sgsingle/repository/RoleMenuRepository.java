package com.chiu.sgsingle.repository;

import com.chiu.sgsingle.entity.RoleMenuEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author mingchiuli
 * @create 2022-11-27 11:53 am
 */
@Repository
public interface RoleMenuRepository extends CrudRepository<RoleMenuEntity, Long> {
}
