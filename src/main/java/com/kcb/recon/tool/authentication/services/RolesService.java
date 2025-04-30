package com.kcb.recon.tool.authentication.services;

import com.kcb.recon.tool.authentication.entities.Role;
import com.kcb.recon.tool.authentication.models.ApproveRejectRequest;
import com.kcb.recon.tool.authentication.models.RoleRequest;
import com.kcb.recon.tool.authentication.models.RolesFilter;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.models.RoleDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public interface RolesService {
    ResponseMessage createRole(RoleRequest request);
    ResponseMessage updateRole(RoleRequest request);
    void updateNoMakerChecker(RoleRequest request);
    Role findRoleById(Long id);
    RoleDetailsDTO getRoleDetailsWithMenus(Long id);
    List<Role> allRolesSuperAdmin();
    Optional<Role> findByRoleName(String name);
    Page<Role> paginatedAdminRolesListWithFilters(RolesFilter filter);
    List<Role> paginatedRolesListWithFilters();
    Page<Role> paginatedRolesListWithFiltersForReviewList(RolesFilter filter);
}
