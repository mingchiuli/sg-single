package com.chiu.sgsingle.service.impl;

import com.chiu.sgsingle.entity.MenuEntity;
import com.chiu.sgsingle.entity.UserEntity;
import com.chiu.sgsingle.repository.MenuRepository;
import com.chiu.sgsingle.service.MenuService;
import com.chiu.sgsingle.service.RoleMenuService;
import com.chiu.sgsingle.service.UserService;
import com.chiu.sgsingle.vo.MenuEntityVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mingchiuli
 * @create 2022-12-04 2:25 am
 */
@Service
public class MenuServiceImpl implements MenuService {

    UserService userService;

    RoleMenuService roleMenuService;

    MenuRepository menuRepository;

    public MenuServiceImpl(UserService userService, RoleMenuService roleMenuService, MenuRepository menuRepository) {
        this.userService = userService;
        this.roleMenuService = roleMenuService;
        this.menuRepository = menuRepository;
    }

    @Override
    public List<MenuEntityVo> getCurrentUserNav(String username) {

        Optional<UserEntity> userEntity = userService.retrieveUserInfo(username);
        String role = userEntity.orElseThrow().getRole();

        List<Long> menuIds = roleMenuService.getNavMenuIds(role);
        Iterable<MenuEntity> menus = menuRepository.findAllById(menuIds);
        ArrayList<MenuEntityVo> entities = new ArrayList<>();
        menus.forEach(menu -> {
            MenuEntityVo entity = new MenuEntityVo();
            BeanUtils.copyProperties(menu, entity);
            entities.add(entity);
        });
        // 转树状结构
        return buildTreeMenu(entities);

    }

    private List<MenuEntityVo> buildTreeMenu(List<MenuEntityVo> menus) {
        //2.组装父子的树形结构
        //2.1 找到所有一级分类
        return menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .peek(menu-> menu.setChildren(getChildren(menu, menus)))
                .sorted(Comparator.comparingInt(menu -> (menu.getOrderNum() == null ? 0 : menu.getOrderNum())))
                .collect(Collectors.toList());
    }

    private List<MenuEntityVo> getChildren(MenuEntityVo root, List<MenuEntityVo> all) {
        return all.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), root.getMenuId()))
                .peek(menu -> menu.setChildren(getChildren(menu, all)))
                .sorted(Comparator.comparingInt(menu -> (menu.getOrderNum() == null ? 0 : menu.getOrderNum())))
                .collect(Collectors.toList());
    }
}
