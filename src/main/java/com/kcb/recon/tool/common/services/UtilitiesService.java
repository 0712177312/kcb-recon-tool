package com.kcb.recon.tool.common.services;

import com.kcb.recon.tool.authentication.models.PermissionRequest;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

@Component
public interface UtilitiesService {
    List<PermissionRequest> getAvailablePermissions();
    String generatePassword(int length, String regexPolicy);

}