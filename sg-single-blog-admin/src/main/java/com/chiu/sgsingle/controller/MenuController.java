package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.entity.MenuEntity;
import com.chiu.sgsingle.jwt.JwtUtils;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.service.MenuService;
import com.chiu.sgsingle.vo.MenuEntityVo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author mingchiuli
 * @create 2022-12-04 2:22 am
 */
@RestController
@RequestMapping("/sys/menu")
@Validated
public class MenuController {

    MenuService menuService;

    JwtUtils jwtUtils;

    public MenuController(MenuService menuService, JwtUtils jwtUtils) {
        this.menuService = menuService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/nav")
    public Result<List<MenuEntityVo>> nav(HttpServletRequest request) {
        String jwt = request.getHeader(jwtUtils.getHeader());
        Claims claim = jwtUtils.getClaimByToken(jwt);
        String username = claim.getSubject();
        List<MenuEntityVo> navs = menuService.getCurrentUserNav(username);
        return Result.success(navs);
    }

    @GetMapping("/info/{id}")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<MenuEntity> info(@PathVariable(name = "id") Long id) {
        MenuEntity menu = menuService.findById(id);
        return Result.success(menu);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<List<MenuEntityVo>> list() {
        List<MenuEntityVo> menus = menuService.tree();
        return Result.success(menus);
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<Object> saveOrUpdate(@Validated @RequestBody MenuEntityVo menu) {
        menuService.save(menu);
        return Result.success();
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole(@highestRoleHolder.getRole())")
    public Result<Object> delete(@PathVariable("id") Long id) {
        menuService.delete(id);
        return Result.success();
    }

}
