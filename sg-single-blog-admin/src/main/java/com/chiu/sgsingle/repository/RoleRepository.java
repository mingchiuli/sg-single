package com.chiu.sgsingle.repository;

import com.chiu.sgsingle.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author mingchiuli
 * @create 2022-11-27 11:52 am
 */
@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
}
