package com.kcb.recon.tool.authentication.services;

import com.kcb.recon.tool.authentication.entities.Permission;
import com.kcb.recon.tool.authentication.models.PermissionRequest;
import com.kcb.recon.tool.common.models.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface PermissionsService {
    ResponseMessage createPermission(PermissionRequest request);
    ResponseMessage updatePermission(PermissionRequest request);
    Permission findPermissionById(Long id);
    List<Permission> allPermissions();
    Page<Permission> paginatedPermissionsList(int page, int size);
}
