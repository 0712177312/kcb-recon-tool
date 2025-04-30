//package com.kcb.recon.tool.configurations.utility;
//
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.kcb.recon.tool.authentication.utils.AppUtillities;
//import com.kcb.recon.tool.configurations.extras.*;
//import com.kcb.recon.tool.configurations.repositories.MenuRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class MenuDataLoader {
//
//    private final MenuRepository menuRepository;
//    private final ObjectMapper objectMapper;
//
//    @PostConstruct
//    public void loadMenuData() {
//        try (InputStream inputStream = getClass().getResourceAsStream("/menus.json")) {
//            // Step 1: Read and map menus from the JSON file
//            List<MenuDTO> menus = objectMapper.readValue(inputStream, new TypeReference<>() {});
//
//            // Step 2: Save menus first
//            List<Menu1> savedMenus = new ArrayList<>();
//            for (MenuDTO menuDTO : menus) {
//                Menu1 menu = new Menu1();
//                menu.setName(menuDTO.getName());
//                menu.setRoute(menuDTO.getRoute());
//                menu.setType(menuDTO.getType());
//                menu.setIcon(menuDTO.getIcon());
//                menu.setBadge(menuDTO.getBadge());
//                menu.setPermissions(new MenuPermission1(menuDTO.getPermissions().getAllowed(), menuDTO.getPermissions().getDenied()));
//
//                // Save the menu
//                savedMenus.add(menuRepository.save(menu));
//            }
//
//            log.info("Successfully loaded {} menus from JSON file", savedMenus.size());
//
//            // Step 3: Save submenus and link them to their parent menus
//            for (MenuDTO menuDTO : menus) {
//                Menu1 parentMenu = savedMenus.stream()
//                        .filter(menu -> menu.getRoute().equals(menuDTO.getRoute()))
//                        .findFirst()
//                        .orElseThrow(() -> new RuntimeException("Menu not found: " + menuDTO.getRoute()));
//
//                List<SubMenu1> subMenus = new ArrayList<>();
//                for (MenuDTO childDTO : menuDTO.getChildren()) {
//                    SubMenu1 subMenu = new SubMenu1();
//                    subMenu.setName(childDTO.getName());
//                    subMenu.setRoute(childDTO.getRoute());
//                    subMenu.setType(childDTO.getType());
//                    subMenu.setIcon(childDTO.getIcon());
//                    subMenu.setBadge(childDTO.getBadge());
//
//                    // Create SubMenuPermission1 instead of MenuPermission1
//                    SubMenuPermission1 subMenuPermission = new SubMenuPermission1(
//                            childDTO.getPermissions().getAllowed(),
//                            childDTO.getPermissions().getDenied()
//                    );
//                    subMenu.setPermissions(subMenuPermission);  // Set SubMenuPermission1
//
//                    subMenu.setMenu(parentMenu);  // Link to parent menu
//
//                    subMenus.add(subMenu);
//                }
//
//                // Save submenus linked to parent menu
//                parentMenu.setChildren(subMenus);  // Set the children relationship
//                menuRepository.save(parentMenu); // Save parent menu with submenus
//            }
//
//            log.info("Successfully loaded submenus from JSON file");
//
//        } catch (Exception e) {
//            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage()
//                    + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
//            log.error("log-message -> {}", logMessage);
//            log.error("Failed to load menu data", e);
//        }
//    }
//
//
//}
