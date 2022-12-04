package com.chiu.sgsingle.controller;

import com.chiu.sgsingle.jwt.JwtUtils;
import com.chiu.sgsingle.lang.Result;
import com.chiu.sgsingle.service.MenuService;
import com.chiu.sgsingle.vo.MenuEntityVo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author mingchiuli
 * @create 2022-12-04 2:22 am
 */
@RestController
@RequestMapping("/sys/menu")
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
}
