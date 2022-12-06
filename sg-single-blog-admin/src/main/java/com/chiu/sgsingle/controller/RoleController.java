package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.entity.RoleEntity;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.service.RoleMenuService;
import com.chiu.sgsingle.service.RoleService;
import com.chiu.sgsingle.service.UserService;
import com.chiu.sgsingle.vo.RoleEntityVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-12-06 8:28 pm
 */
@RestController
@RequestMapping("/sys/role")
@Validated
public class RoleController {

    RoleService roleService;

    UserService userService;

    RoleMenuService roleMenuService;

    public RoleController(RoleService roleService, UserService userService, RoleMenuService roleMenuService) {
        this.roleService = roleService;
        this.userService = userService;
        this.roleMenuService = roleMenuService;
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<RoleEntity> info(@PathVariable("id") Long id) {
        RoleEntity role = roleService.info(id);
        return Result.success(role);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole(@highestRoleHolder.getRole(), @defaultRoleHolder.getRole())")
    public Result<PageAdapter<RoleEntity>> listPage(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "5") Integer pageSize) {
        PageAdapter<RoleEntity> pageData = roleService.listPage(currentPage, pageSize);
        return Result.success(pageData);
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<Void> saveOrUpdate(@Validated @RequestBody RoleEntityVo role) {
        roleService.saveOrUpdate(role);
        return Result.success();
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<Void> delete(@RequestBody List<Long> ids) {
        roleService.delete(ids);
        return Result.success();
    }

    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<List<Long>> info(@PathVariable("roleId") Long roleId, @RequestBody List<Long> menuIds) {
        menuIds = roleService.perm(roleId, menuIds);
        return Result.success(menuIds);
    }


}
