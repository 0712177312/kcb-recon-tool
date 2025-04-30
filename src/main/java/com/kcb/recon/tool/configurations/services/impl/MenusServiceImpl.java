package com.kcb.recon.tool.configurations.services.impl;

import com.kcb.recon.tool.authentication.utils.AppUtillities;
import com.kcb.recon.tool.configurations.extras.Menu1;
import com.kcb.recon.tool.configurations.extras.MenuPermission1;
import com.kcb.recon.tool.configurations.extras.SubMenu1;
import com.kcb.recon.tool.configurations.extras.SubMenuPermission1;
import com.kcb.recon.tool.configurations.repositories.CommonRepository;
import com.kcb.recon.tool.configurations.repositories.MenuRepository;
import com.kcb.recon.tool.configurations.services.MenusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class MenusServiceImpl implements MenusService {


    private final MenuRepository menuRepository;
    private final CommonRepository commonRepository;

    public MenusServiceImpl(MenuRepository menuRepository, CommonRepository commonRepository1) {
        this.menuRepository = menuRepository;
        this.commonRepository = commonRepository1;
    }


    public List<Menu1> loadMenusFromDatabase() {
        List<Menu1> menuList = new ArrayList<>();
        try {
            menuList = menuRepository.findAll();
        } catch (Exception e) {
            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage()
                    + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
            log.error("log-message -> {}", logMessage);
        }
        return menuList;
    }


    @Override
    public List<Menu1> findMenusAndSubMenusByListOfRoles(List<String> roles) {
        List<Menu1> accessibleMenus = new ArrayList<>();

        if (roles != null && !roles.isEmpty()) {
            List<Menu1> allParentMenus = loadMenusFromDatabase();
            log.info("Loading all menus from database | {}", allParentMenus.size());

            for (Menu1 menu : allParentMenus) {
                MenuPermission1 menuPermission = commonRepository.getMenuPermission(menu.getId());
                log.info("menuPermission | {}", menuPermission.toString());

                if (isMenuAccessible(menuPermission, roles)) {
                    menu.setPermissions(menuPermission);
                    menu.setChildren(findSubMenusByListOfRoles(menu.getId(), roles));
                    accessibleMenus.add(menu);
                }
            }
        }

        return accessibleMenus;
    }
    public List<SubMenu1> findSubMenusByListOfRoles(Long menuID, List<String> roles) {
        List<SubMenu1> accessibleSubMenus = new ArrayList<>();
        List<SubMenu1> allSubMenus = commonRepository.getSubmenus(menuID);

        for (SubMenu1 subMenu : allSubMenus) {
            SubMenuPermission1 subMenuPermission = commonRepository.getSubMenuPermission(subMenu.getId());

            if (isSubMenuAccessible(subMenuPermission, roles)) {
                subMenu.setPermissions(subMenuPermission);
                accessibleSubMenus.add(subMenu);
            }
        }

        return accessibleSubMenus;
    }

    @Override
    public List<Menu1> findAll() {
        return menuRepository.findAll();
    }
    private boolean isMenuAccessible(MenuPermission1 permission, List<String> roles) {
        if (permission == null || permission.getAllowed() == null || permission.getAllowed().isEmpty()) {
            return false; // no allowed roles defined
        }

        return roles.stream().anyMatch(permission.getAllowed()::contains);
    }

    private boolean isSubMenuAccessible(SubMenuPermission1 permission, List<String> roles) {
        if (permission == null || permission.getAllowed() == null || permission.getAllowed().isEmpty()) {
            return false;
        }

        return roles.stream().anyMatch(permission.getAllowed()::contains);
    }

}
