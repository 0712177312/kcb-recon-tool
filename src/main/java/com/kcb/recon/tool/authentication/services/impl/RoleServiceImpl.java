package com.kcb.recon.tool.authentication.services.impl;

import com.kcb.recon.tool.authentication.entities.Permission;
import com.kcb.recon.tool.authentication.entities.Role;
import com.kcb.recon.tool.authentication.models.RoleRequest;
import com.kcb.recon.tool.authentication.models.RolesFilter;
import com.kcb.recon.tool.authentication.repositories.RolesRepository;
import com.kcb.recon.tool.authentication.services.PermissionsService;
import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.authentication.utils.AppUtillities;
import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.google.gson.Gson;
import com.kcb.recon.tool.common.models.RoleDetailsDTO;
import com.kcb.recon.tool.configurations.extras.Menu1;
import com.kcb.recon.tool.configurations.extras.SubMenu1;
import com.kcb.recon.tool.configurations.repositories.CommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RoleServiceImpl implements RolesService {

    private final RolesRepository rolesRepository;
    private final CommonRepository commonRepository;
    private final PermissionsService permissionsService;

    public RoleServiceImpl(RolesRepository rolesRepository,
                           CommonRepository commonRepository,
                           PermissionsService permissionsService) {
        this.rolesRepository = rolesRepository;
        this.commonRepository = commonRepository;
        this.permissionsService = permissionsService;
    }

    @Override
    public ResponseMessage createRole(RoleRequest request) {
        log.info("Inside createRole(RoleRequest request) Method At {}", new Date());
        log.info("Create Role Request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var role = new Role();
        var exists = rolesRepository.findByName(request.getName());
        if (exists.isPresent()) {
            log.warn("Failed to create role ! Role {} Already exists!", request.getName());
            res.setMessage("Role " + request.getName() + " Already exists!");
            res.setStatus(false);
        } else {
            role.setName(request.getName());
            if (request.getUserName().equalsIgnoreCase("System")) {
                role.setStatus(RecordStatus.Active.name());
                role.setValidityStatus(ValidityStatus.Approved.name());
                role.setCheckedBy("System");
                role.setCheckedOn(new Date());
            } else {
                role.setStatus(RecordStatus.Active.name());
                role.setValidityStatus(ValidityStatus.Approved.name());
            }
            Set<Permission> privileges = new HashSet<>();
            if (request.getPermissions().stream().findAny().isPresent()) {
                for (var pi : request.getPermissions()) {
                    var p = permissionsService.findPermissionById(pi);
                    if (p != null) {
                        privileges.add(p);
                    }
                }
                role.setPermissions(privileges);
            }
            role.setCreatedBy(request.getUserName());
            rolesRepository.save(role);
            // Insert menus and submenus into respective tables
            insertMenusAndSubmenus(request);
            res.setStatus(true);
            res.setData(null);
            log.info("Role {} Created Successfully!", request.getName());
            res.setMessage("Created Successfully!");
        }
        return res;
    }

    private void insertMenusAndSubmenus(RoleRequest request) {

        for (String menuName : request.getMenus()) {
            Integer menuId = commonRepository.findIdByMenuName(menuName);
            log.info("menu_id | {}", menuId);
            commonRepository.insertMenu(menuId, request.getName());
        }

        for (String submenuName : request.getSubmenus()) {
            Integer submenuId = commonRepository.findIdBySubMenuName(submenuName);
            log.info("submenu_id | {}", submenuId);
            commonRepository.insertSubMenu(submenuId, request.getName());
        }
    }

    @Override
    public ResponseMessage updateRole(RoleRequest request) {
        log.info("Updating Role Request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = rolesRepository.findById(request.getId());

        if (exists.isPresent()) {
            var role = exists.get();
            role.setChangeStatus(ChangeStatus.Pending.name());
            role.setNewValues(new Gson().toJson(request));
            role.setModifiedBy(request.getUserName());
            role.setModifiedOn(new Date());
            if (request.isStatus()) {
                role.setStatus(RecordStatus.Active.name());
            } else {
                role.setStatus(RecordStatus.Inactive.name());
            }
            Set<Permission> privileges = role.getPermissions();
            Set<Long> incomingPrivilegeIds = new HashSet<>(request.getPermissions());

            for (var pi : request.getPermissions()) {
                var permission = permissionsService.findPermissionById(pi);
                if (permission != null) {
                    privileges.add(permission);
                }
            }
            privileges.removeIf(privilege -> !incomingPrivilegeIds.contains(privilege.getId()));
            role.setPermissions(privileges);
            rolesRepository.save(role);
            insertSubmenus(request);
            res.setStatus(true);
            res.setData(null);
            res.setMessage("Submitted Successfully!");
            log.info("Role update initiated successfully! Role Name {} ", request.getName());
        } else {
            log.warn("Failed to initiate role update {} | Role does not exist!", request.getName());
            res.setMessage("Role does not exist!");
            res.setStatus(false);
        }
        return res;
    }
    private void insertSubmenus(RoleRequest request) {
        List<String> submenuIds = request.getSubmenus();
        List<String> menuNames = request.getMenus();
        log.info("submenu_ids, role-name is | {}, {}", submenuIds, request.getName());
        String roleName = request.getName();
        // Delete existing submenu and menu mappings
        if (commonRepository.getAllWithRoleName(roleName)) {
            try {
                boolean deletedSubmenus = commonRepository.deleteFromSubMenu(roleName);
                boolean deletedMenus = commonRepository.deleteFromMenu(roleName);

                log.info("Deleted submenus? -> {}", deletedSubmenus);
                log.info("Deleted menus? -> {}", deletedMenus);
            } catch (Exception e) {
                String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage()
                        + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
                log.error("Exception during deletion -> {}", logMessage);
            }
        } else {
            log.info("No existing submenu/menu mappings found for role -> {}", roleName);
        }
        for (String id : submenuIds) {
            try {
                int submenuId = Integer.parseInt(id);
                commonRepository.insertSubMenu(submenuId, request.getName());
            } catch (NumberFormatException e) {
                log.warn("Invalid submenu ID -> {}", id);
            }
        }
        // Insert new menu mappings
        for (String menuName : menuNames) {
            Integer menuId = commonRepository.findIdByMenuName(menuName.trim());
            if (menuName != null && !menuName.trim().isEmpty()) {
                commonRepository.insertMenu(menuId, roleName);
            } else {
                log.warn("Empty or null menu name skipped for role -> {}", roleName);
            }
        }
    }

    @Override
    public void updateNoMakerChecker(RoleRequest request) {
        log.info("Inside updateNoMakerChecker(RoleRequest request) Method At {}", new Date());
        log.info("Updating Role Request  {} | ", new Gson().toJson(request));
        var exists = rolesRepository.findById(request.getId());
        if (exists.isPresent()) {
            var role = exists.get();
            role.setName(request.getName());
            role.setStatus(RecordStatus.Active.name());
            role.setValidityStatus(ValidityStatus.Approved.name());
            if (role.getCreatedBy().equalsIgnoreCase("System")) {
                role.setChangeStatus(ChangeStatus.Approved.name());
            } else {
                role.setChangeStatus(ChangeStatus.Pending.name());
            }
            role.setNewValues(new Gson().toJson(request));
            role.setModifiedBy(request.getUserName());
            role.setModifiedOn(new Date());
            Set<Permission> privileges = role.getPermissions();
            Set<Long> incomingPrivilegeIds = new HashSet<>(request.getPermissions());
            for (var pi : request.getPermissions()) {
                var p = permissionsService.findPermissionById(pi);
                if (p != null) {
                    privileges.add(p);
                }
            }
            privileges.removeIf(privilege -> !incomingPrivilegeIds.contains(privilege.getId()));
            role.setPermissions(privileges);
            rolesRepository.save(role);
        } else {
            log.warn("Failed to update role {} | Role does not exist!", request.getName());
        }
    }


    @Override
    public Role findRoleById(Long id) {
        log.info("Inside findRoleById(Long id) At {}", new Date());
        return rolesRepository.findById(id).orElse(null);
    }

    @Override
    public RoleDetailsDTO getRoleDetailsWithMenus(Long roleId) {
        Role role = rolesRepository.findById(roleId).orElse(null);
        if (role == null) return null;
        String roleName = role.getName();
        log.info("Inside getRoleDetailsWithMenus(Long roleId) At {}", new Date());
        log.info("Role Name {} ", roleName);
        List<Long> menuIds = commonRepository.findMenuIdsByRoleName(roleName);
        log.info("menuIds : {}", menuIds);
        List<Long> subMenuIds = commonRepository.findSubMenuIdsByRoleName(roleName);
        log.info("subMenuIds : {}", subMenuIds);
        List<Menu1> menus = commonRepository.findByMenuIdIn(menuIds);
        log.info("menus: {}", new Gson().toJson(menus));
        for (Menu1 menu : menus) {
            if (subMenuIds != null && !subMenuIds.isEmpty()) {
                List<SubMenu1> subMenus = commonRepository.findBySubMenuMenuIdIn(menu.getId(), subMenuIds);
                log.info("subMenus : {}", new Gson().toJson(subMenus));
                if (subMenus != null && !subMenus.isEmpty()) {
                    menu.setChildren(subMenus);
                } else {
                    menu.setChildren(Collections.emptyList());
                }
            }
        }
        RoleDetailsDTO roleDetails = new RoleDetailsDTO();
        roleDetails.setId(role.getId());
        roleDetails.setName(role.getName());
        roleDetails.setCreatedOn(role.getCreatedOn());
        roleDetails.setCreatedBy(role.getCreatedBy());
        roleDetails.setStatus(role.getStatus());
        roleDetails.setMenus(menus);
        log.info("roleDetails : {}", new Gson().toJson(roleDetails));
        return roleDetails;
    }


    @Override
    public List<Role> allRolesSuperAdmin() {
        return rolesRepository.allWithoutPaginationForSuperAdmin();
    }

    @Override
    public Optional<Role> findByRoleName(String name) {
        return rolesRepository.findByName(name);
    }

    @Override
    public Page<Role> paginatedAdminRolesListWithFilters(RolesFilter request) {
        String status = request.getStatus();
        if (status != null && !status.isEmpty()) {
            return rolesRepository.filterAdminRolesWithPaginationStatusProvided(status, PageRequest.of(request.getPage(), request.getSize()));
        }
        return rolesRepository.allAdminRolesWithPagination(PageRequest.of(request.getPage(), request.getSize()));
    }


    @Override
    public List<Role> paginatedRolesListWithFilters() {
        return rolesRepository.allWithoutPagination();
    }


}