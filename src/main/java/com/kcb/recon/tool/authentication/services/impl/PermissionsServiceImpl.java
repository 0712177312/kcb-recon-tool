package com.kcb.recon.tool.authentication.services.impl;

import com.kcb.recon.tool.authentication.entities.Permission;
import com.kcb.recon.tool.authentication.models.PermissionRequest;
import com.kcb.recon.tool.authentication.repositories.PermissionsRepository;
import com.kcb.recon.tool.authentication.services.PermissionsService;
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
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PermissionsServiceImpl implements PermissionsService {

    @Autowired
    private PermissionsRepository permissionsRepository;

    @Override
    public ResponseMessage createPermission(PermissionRequest request) {
        log.info("Inside createPermission(PermissionRequest request) At {} ", new Date());
        log.info("Create Permission Request {} ",new Gson().toJson(request));
        var res = new ResponseMessage();
        var Permission = new Permission();
        var exists = permissionsRepository.findByName(request.getName());
        if (exists.isPresent()) {
            log.warn("Failed to create Permission ! Permission {} Already exists!", request.getName());
            res.setMessage("Permission " + request.getName() + " Already exists!");
            res.setStatus(false);
        } else {
            Permission.setDescription(request.getDescription());
            Permission.setCreatedBy(request.getUserName());
            Permission.setName(request.getName());
            Permission.setStatus(RecordStatus.Active.name());
            Permission.setValidityStatus(ValidityStatus.Approved.name());
            permissionsRepository.save(Permission);
            res.setStatus(true);
            res.setData(null);
            log.info("Permission {} ",request.getName()+" created successfully!");
            res.setMessage("Created Successfully!");
        }
        return res;
    }

    @Override
    public ResponseMessage updatePermission(PermissionRequest request) {
        log.info("Inside updatePermission(PermissionRequest request) {}", new Date());
        log.info("Update Permission Request {} ",new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = permissionsRepository.findById(request.getId());
        if (exists.isPresent()) {
            var Permission = exists.get();
            Permission.setName(request.getName());
            Permission.setDescription(request.getDescription());
            Permission.setModifiedBy(request.getUserName());
            Permission.setModifiedOn(new Date());
            Permission.setChangeStatus(ChangeStatus.Approved.name());
            permissionsRepository.save(Permission);
            res.setStatus(true);
            res.setData(null);
            log.info("Permission {} updated successfully!", request.getName());
            res.setMessage("Updated Successfully!");
        } else {
            res.setMessage("Permission Does not exist!");
            res.setStatus(false);
            log.warn("Failed to update Permission {} | Permission does not exist!", request.getName());
        }
        return res;
    }

    @Override
    public Permission findPermissionById(Long id) {
        log.info("Inside findPermissionById(Long id) {} ",new Date());
        return permissionsRepository.findById(id).orElse(null);
    }

    @Override
    public List<Permission> allPermissions() {
        log.info("Inside allPermissions() {} ",new Date());
        log.info("Fetch All Permissions without pagination");
        return permissionsRepository.allPermissionsNoPagination();
    }

    @Override
    public Page<Permission> paginatedPermissionsList(int page, int size) {
        log.info("Inside paginatedPermissionsList(int page, int size) {} ",new Date());
        log.info("Fetching all Permissions with pagination!");
        return permissionsRepository.Paginated(PageRequest.of(page, size));
    }
}