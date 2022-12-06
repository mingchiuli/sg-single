package com.chiu.sgsingle.repository;

import com.chiu.sgsingle.entity.RoleMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-11-27 11:53 am
 */
@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenuEntity, Long> {
    @Query(value = "SELECT menu_id from m_role_menu where role_id = ?1", nativeQuery = true)
    List<Long> findMenuIdsByRoleId(Long id);

    void deleteByRoleId(Long roleId);
}
