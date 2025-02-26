package com.kcb.recon.tool.authentication.services.impl;

import com.kcb.recon.tool.authentication.entities.Permission;
import com.kcb.recon.tool.authentication.entities.Role;
import com.kcb.recon.tool.authentication.models.ApproveRejectRequest;
import com.kcb.recon.tool.authentication.models.RoleRequest;
import com.kcb.recon.tool.authentication.models.RolesFilter;
import com.kcb.recon.tool.authentication.repositories.RolesRepository;
import com.kcb.recon.tool.authentication.services.PermissionsService;
import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RoleServiceImpl implements RolesService {
    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PermissionsService permissionsService;

    @Override
    public ResponseMessage createRole(RoleRequest request) {
        log.info("Inside createRole(RoleRequest request) Method At {}", new Date());
        log.info("Create Role Request {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var role = new Role();
        var exists = rolesRepository.findByNameAndOrganization(request.getName(), request.getOrganization());
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
                role.setOrganization(request.getOrganization());
                role.setStatus(RecordStatus.Inactive.name());
                role.setValidityStatus(ValidityStatus.Pending.name());
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
            res.setStatus(true);
            res.setData(null);
            log.info("Role {} Created Successfully!", request.getName());
            res.setMessage("Created Successfully!");
        }
        return res;
    }

    @Override
    public ResponseMessage updateRoleWithMakerChecker(RoleRequest request) {
        log.info("Inside updateRoleWithMakerChecker(RoleRequest request) Method At {}", new Date());
        log.info("Updating Role Request with maker checker enabled {} ", new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = rolesRepository.findById(request.getId());

        if (exists.isPresent()) {
            var role = exists.get();
            role.setChangeStatus(ChangeStatus.Pending.name());
            role.setNewValues(new Gson().toJson(request));
            role.setModifiedBy(request.getUserName());
            role.setModifiedOn(new Date());
            var currRole = new Gson().toJson(role);
            var updatedRole = buildUpdatedRoleBody(currRole,request);
            role.setChangedValues(updatedRole);
            rolesRepository.save(role);
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

    public String buildUpdatedRoleBody(String role,RoleRequest rr){
        Role newRole = new Gson().fromJson(role, Role.class);
        newRole.setName(rr.getName());
        newRole.setChangedValues(null);
        if(rr.isStatus()) {
            newRole.setStatus(RecordStatus.Active.name());
        }
        else{
            newRole.setStatus(RecordStatus.Inactive.name());
        }
        Set<Permission> privileges = newRole.getPermissions();
        Set<Long> incomingPrivilegeIds = new HashSet<>(rr.getPermissions());

        for (var pi : rr.getPermissions()) {
            var permission = permissionsService.findPermissionById(pi);
            if (permission != null) {
                privileges.add(permission);
            }
        }
        privileges.removeIf(privilege -> !incomingPrivilegeIds.contains(privilege.getId()));
        newRole.setPermissions(privileges);
        return new Gson().toJson(newRole);
    }

    @Override
    public void updateNoMakerChecker(RoleRequest request) {
        log.info("Inside updateNoMakerChecker(RoleRequest request) Method At {}", new Date());
        log.info("Updating Role Request with no maker-checker {} ", new Gson().toJson(request));
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
            log.info("Role {} updated successfully with no maker-checker!", request.getName());
        } else {
            log.warn("Failed to update role {} | Role does not exist!", request.getName());
        }
    }

    @Override
    public ResponseMessage approveRejectRoleModification(ApproveRejectRequest request) {
        log.info("Inside approveRejectRoleModification(ApproveRejectRequest request) Method At {}", new Date());
        log.info("Checker Request to approve/reject role modification {} ", new Gson().toJson(request));

        var res = new ResponseMessage();
        var exists = rolesRepository.findById(request.getRecordId());

        if (exists.isPresent()) {
            var role = exists.get();
            RoleRequest rr = new Gson().fromJson(role.getNewValues(), RoleRequest.class);

            if (rr != null) {
                role.setModificationsCheckedOn(new Date());
                role.setModificationsCheckedBy(request.getCheckerName());


                if (request.isApprove()) {
                    if (rr.isStatus()) {
                        role.setStatus(RecordStatus.Active.name());
                        role.setValidityStatus(ValidityStatus.Approved.name());
                    } else {
                        role.setStatus(RecordStatus.Inactive.name());
                    }
                    role.setName(rr.getName());
                    role.setChangeStatus(ChangeStatus.Approved.name());
                    role.setNewValues(null);

                    Set<Permission> privileges = role.getPermissions();
                    Set<Long> incomingPrivilegeIds = new HashSet<>(rr.getPermissions());

                    for (var pi : rr.getPermissions()) {
                        var p = permissionsService.findPermissionById(pi);
                        if (p != null) {
                            privileges.add(p);
                        }
                    }
                    privileges.removeIf(privilege -> !incomingPrivilegeIds.contains(privilege.getId()));
                    role.setPermissions(privileges);
                    role.setRemarks("Approved - " + request.getCheckerName());

                    // Save only if approved
                    rolesRepository.save(role);
                    log.info("Role {} approved and updated successfully!", rr.getName());
                } else {
                    role.setRemarks(request.getRemarks());
                    role.setChangeStatus(ChangeStatus.Disapproved.name());
                    log.info("Role {} disapproved by {}", rr.getName(), request.getCheckerName());

                    // Persist only auditing fields if not approved
                    role.setStatus(role.getStatus());
                    role.setValidityStatus(role.getValidityStatus());
                    rolesRepository.save(role);
                }

                res.setStatus(true);
                res.setData(null);
                res.setMessage("Updated Successfully!");
            }
        } else {
            log.warn("Failed to approve/reject role | Role does not exist!");
            res.setMessage("Role does not exist!");
            res.setStatus(false);
        }

        return res;
    }


    @Override
    public ResponseMessage approveRejectRole(ApproveRejectRequest request) {
        log.info("Inside approveRejectRole(ApproveRejectRequest request) Method At {}", new Date());
        log.info("Approving Role Request {} ", new Gson().toJson(request));
        var exists = rolesRepository.findById(request.getRecordId());
        var res = new ResponseMessage();
        if (exists.isPresent()) {
            var role = exists.get();
            role.setCheckedBy(request.getCheckerName());
            role.setCheckedOn(new Date());
            if (request.isApprove()) {
                role.setStatus(RecordStatus.Active.name());
                role.setValidityStatus(ValidityStatus.Approved.name());
                role.setRemarks("Approved - " + request.getCheckerName());
            } else {
                role.setStatus(RecordStatus.Inactive.name());
                role.setValidityStatus(ValidityStatus.Disapproved.name());
                role.setRemarks(request.getRemarks());
            }
            res.setMessage("Processed Successfully!");
            res.setStatus(true);
            res.setData(null);
            rolesRepository.save(role);
            log.info("Approve/Reject Role completed successfully by {} ", request.getCheckerName());
        } else {
            res.setMessage("Role Does Not Exist!");
            res.setStatus(false);
            res.setData(null);
            log.warn("Failed to approve/reject role | Role does not exist! Cannot process request!");
        }
        return res;
    }

    @Override
    public Role findRoleById(Long id) {
        log.info("Inside findRoleById(Long id) At {}", new Date());
        return rolesRepository.findById(id).orElse(null);
    }

    @Override
    public List<Role> allRolesAdmin(Long organizationId) {
        log.info("Inside allRolesAdmin(Long organizationId) At {}", new Date());
        log.info("Fetch All Roles without pagination");
        return rolesRepository.allWithoutPaginationForAdmin(organizationId);
    }

    @Override
    public List<Role> allRolesPerOrganization() {
        log.info("Inside allRolesPerOrganization(Long organizationId) At {}", new Date());
        log.info("Fetch All Roles without pagination per organization (both active and inactive)");
        return rolesRepository.allWithoutPaginationForOrganizationNoFilter();
    }

    @Override
    public List<Role> allRolesSuperAdmin() {
        log.info("Inside allRolesSuperAdmin() At {}", new Date());
        log.info("Fetch All Roles without pagination by super admin");
        return rolesRepository.allWithoutPaginationForSuperAdmin();
    }

    @Override
    public Optional<Role> findByRoleName(String name) {
        log.info("Inside findByRoleName(String name) At {}", new Date());
        return rolesRepository.findByName(name);
    }

    @Override
    public Page<Role> paginatedAdminRolesListWithFilters(RolesFilter request) {
        log.info("Inside paginatedAdminRolesListWithFilters(RolesFilter request) At -> {} ", new Date());
        log.info("Fetch admin roles with pagination and filters {} ", new Gson().toJson(request));
        String status = request.getStatus();
        if (status != null && !status.isEmpty()) {
            return rolesRepository.filterAdminRolesWithPaginationStatusProvided(status, PageRequest.of(request.getPage(), request.getSize()));
        }
        return rolesRepository.allAdminRolesWithPagination(PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public Page<Role> paginatedRolesListWithFilters(RolesFilter request) {
        log.info("Inside paginatedRolesListWithFilters(RolesFilter filter) At -> {} ", new Date());
        log.info("Fetch roles with pagination and filters {} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long organization = request.getOrganization();

        if (status != null && !status.isEmpty()) {
            return rolesRepository.filterWithPaginationStatusProvided(organization,status, PageRequest.of(request.getPage(), request.getSize()));
        }
        return rolesRepository.allWithPagination(organization,PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public Page<Role> paginatedRolesListWithFiltersForReviewList(RolesFilter request) {
        log.info("Inside paginatedRolesListWithFiltersForReviewList(RolesFilter filter) At -> {} ", new Date());
        log.info("Fetching roles with pagination and filters for review list (maker-checker) {} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long organization = request.getOrganization();

        if (status != null && !status.isEmpty()) {
            return rolesRepository.filterWithPaginationStatusProvidedForReviewList(organization,status, PageRequest.of(request.getPage(), request.getSize()));
        }
        return rolesRepository.filterWithPaginationForReviewListPendingOnly(organization,PageRequest.of(request.getPage(), request.getSize()));
    }

    @Override
    public Page<Role> paginatedRolesListWithFiltersForModificationsReviewList(RolesFilter request) {
        log.info("Inside paginatedRolesListWithFiltersForModificationsReviewList(RolesFilter filter) At -> {} ", new Date());
        log.info("Fetching roles with pagination and filters for modifications review list (maker-checker) {} ", new Gson().toJson(request));
        String status = request.getStatus();
        Long organization = request.getOrganization();

        if (status != null && !status.isEmpty()) {
            return rolesRepository.filterWithPaginationStatusProvidedForModificationsReviewList(organization,status, PageRequest.of(request.getPage(), request.getSize()));
        }
        return rolesRepository.filterWithPaginationForModificationsReviewListPendingOnly(organization,PageRequest.of(request.getPage(), request.getSize()));
    }
}