package com.kcb.recon.tool.common.services.impl;

import com.kcb.recon.tool.authentication.models.PermissionRequest;
import com.kcb.recon.tool.authentication.models.RoleRequest;
import com.kcb.recon.tool.authentication.models.UserAccountRequest;
import com.kcb.recon.tool.authentication.services.PermissionsService;
import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.services.InitializeAppService;
import com.kcb.recon.tool.common.services.UtilitiesService;
import com.kcb.recon.tool.authentication.entities.Permission;
import com.kcb.recon.tool.configurations.models.SubsidiaryRequest;
import com.kcb.recon.tool.configurations.services.SubsidiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitializeAppServiceImpl implements InitializeAppService {

    private final SubsidiaryService subsidiaryService;
    @Value("${org.default.admin.email}")
    private String defaultEmail;

    @Value("${org.default.admin.role}")
    private String defaultUserRole;

    @Value("${org.default.superadmin.role}")
    private String defaultSuperadminRole;

    @Value("${org.default.admin.phone}")
    private String defaultMobile;

    @Value("${org.default.admin.username}")
    private String defaultUsername;

    @Value("${org.default.user-account-types}")
    private String defaultAccountTypes;

    @Value("${org.default.country.code}")
    private String defaultCountryCode;

    @Value("${org.default.country.name}")
    private String defaultCountryName;

    private final UsersService usersService;
    private final RolesService rolesService;
    private final PermissionsService permissionsService;
    private final UtilitiesService utilitiesService;

    @Override
    public void InitializeApplication() {
        log.info("Inside InitializeApplication()");
        log.info("Initializing Application At -> {} ", new Date());
        log.info("Initializing Permissions");
        List<PermissionRequest> Permissions = utilitiesService.getAvailablePermissions();
        for (var p : Permissions) {
            var pr = new PermissionRequest();
            pr.setName(p.getName());
            pr.setDescription(p.getDescription());
            pr.setUserName("System");
            permissionsService.createPermission(pr);
        }

        log.info("Done creating / Updating Permissions");
        List<Long> pvs = permissionsService.allPermissions()
                .stream()
                .map(Permission::getId)
                .collect(Collectors.toList());

        if (rolesService.allRolesSuperAdmin().isEmpty()) {
            log.info("Creating default role for super admin");
            var saRole = new RoleRequest();
            saRole.setName(defaultSuperadminRole);
            saRole.setUserName("System");
            saRole.setPermissions(pvs);
            rolesService.createRole(saRole);
            log.info("Done Creating default super admin role -> {} ", defaultSuperadminRole);

            var adminRole = new RoleRequest();
            pvs = new ArrayList<>();
            adminRole.setName(defaultUserRole);
            adminRole.setUserName("System");
            adminRole.setPermissions(pvs);
            rolesService.createRole(adminRole);
            log.info("Done Creating default country admin role -> {} ", defaultUserRole);
        } else {
            log.info("Updating super admin's permissions");
            var r = rolesService.findByRoleName(defaultSuperadminRole);
            if (r.isPresent()) {
                var role = new RoleRequest();
                role.setName(r.get().getName());
                role.setUserName(r.get().getCreatedBy());
                role.setPermissions(pvs);
                role.setId(r.get().getId());
                rolesService.updateNoMakerChecker(role);
                log.info("Done updating default role's permissions -> {} ", defaultSuperadminRole);
            }
        }

        if (subsidiaryService.allCountriesWithoutPagination().isEmpty()) {
            log.info("Creating default country");
            var countryRequest = new SubsidiaryRequest();
            countryRequest.setCreatedBy("System");
            countryRequest.setCompanyName(defaultCountryName);
            countryRequest.setStatus(RecordStatus.Active.name());
            countryRequest.setCompanyCode(defaultCountryCode);
            subsidiaryService.createSubsidiary(countryRequest);
            log.info("Default Country {} Created successfully!", defaultCountryName);
        }

        if (usersService.allUserAccounts().isEmpty()) {
            var u = new UserAccountRequest();
            var role = rolesService.findByRoleName(defaultSuperadminRole);
            role.ifPresent(value -> u.getRoles().add(value.getId()));
            u.setCompanyName(defaultCountryName);
            u.setUsername(defaultUsername);
            u.setAdminName("System");
            u.setFirstName("Admin");
            u.setOtherNames("Admin");
            u.setPhoneNumber(defaultMobile);
            u.setEmailAddress(defaultEmail);
            u.setGender("Male");
            usersService.registerSuperadmin(u);
        }
    }
}
