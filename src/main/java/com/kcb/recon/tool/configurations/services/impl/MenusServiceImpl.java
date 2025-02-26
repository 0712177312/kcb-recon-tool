package com.kcb.recon.tool.configurations.services.impl;

import com.kcb.recon.tool.configurations.models.Menu;
import com.kcb.recon.tool.configurations.models.MenuPermission;
import com.kcb.recon.tool.configurations.models.SubMenu;
import com.kcb.recon.tool.configurations.models.SubMenuPermission;
import com.kcb.recon.tool.configurations.services.MenusService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MenusServiceImpl implements MenusService {

    @Value("${menus.file.path}")
    private String menusFile;


    public List<Menu> loadMenusFromFile() {
        log.info("Loading menus from JSON file at path: {}", menusFile);
        try (FileReader fileReader = new FileReader(Paths.get(menusFile).toFile())) {
            Gson gson = new Gson();
            return List.of(gson.fromJson(fileReader, Menu[].class));
        } catch (IOException e) {
            log.error("Failed to load menus from JSON file. Error: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Menu> findMenusAndSubMenusByListOfRoles(List<String> roles) {
        log.info("Inside findMenusAndSubMenusByListOfRoles(List<String> roles) At {}", new Date());
        log.info("Fetching all parent menus and filtering by roles...");

        List<Menu> accessibleMenus = new ArrayList<>();

        if (roles != null && !roles.isEmpty()) {
            List<Menu> allParentMenus = loadMenusFromFile();

            for (Menu menu : allParentMenus) {
                List<SubMenu> accessibleSubMenus = menu.getChildren()
                        .stream()
                        .filter(subMenu -> isSubMenuAccessibleByRoles(subMenu, roles))
                        .toList();

                if (!accessibleSubMenus.isEmpty() || isParentAccessibleByRoles(menu, roles)) {
                    menu.setChildren(accessibleSubMenus);
                    accessibleMenus.add(menu);
                }
            }
        }
        return accessibleMenus;
    }

    private boolean isParentAccessibleByRoles(Menu menu, List<String> roles) {
        MenuPermission permissions = menu.getPermissions();
        if (permissions != null && permissions.getOnly() != null && !permissions.getOnly().isEmpty()) {
            return roles.stream().anyMatch(permissions.getOnly()::contains);
        }
        return true;
    }

    private boolean isSubMenuAccessibleByRoles(SubMenu subMenu, List<String> roles) {
        SubMenuPermission permissions = subMenu.getPermissions();
        if (permissions != null && permissions.getOnly() != null && !permissions.getOnly().isEmpty()) {
            return roles.stream().anyMatch(permissions.getOnly()::contains);
        }
        return true;
    }
}