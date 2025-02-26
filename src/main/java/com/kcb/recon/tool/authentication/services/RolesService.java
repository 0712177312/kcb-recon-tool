package com.kcb.recon.tool.authentication.services;

import com.kcb.recon.tool.authentication.entities.Role;
import com.kcb.recon.tool.authentication.models.ApproveRejectRequest;
import com.kcb.recon.tool.authentication.models.RoleRequest;
import com.kcb.recon.tool.authentication.models.RolesFilter;
import com.kcb.recon.tool.common.models.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public interface RolesService {
    ResponseMessage createRole(RoleRequest request);
    ResponseMessage updateRoleWithMakerChecker(RoleRequest request);
    void updateNoMakerChecker(RoleRequest request);
    ResponseMessage approveRejectRoleModification(ApproveRejectRequest request);
    ResponseMessage approveRejectRole(ApproveRejectRequest request);
    Role findRoleById(Long id);
    List<Role> allRolesAdmin(Long organizationId);
    List<Role> allRolesPerOrganization();
    List<Role> allRolesSuperAdmin();
    Optional<Role> findByRoleName(String name);
    Page<Role> paginatedAdminRolesListWithFilters(RolesFilter filter);
    Page<Role> paginatedRolesListWithFilters(RolesFilter filter);
    Page<Role> paginatedRolesListWithFiltersForReviewList(RolesFilter filter);
    Page<Role> paginatedRolesListWithFiltersForModificationsReviewList(RolesFilter filter);
}
