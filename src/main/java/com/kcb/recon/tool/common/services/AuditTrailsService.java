package com.kcb.recon.tool.common.services;

import com.kcb.recon.tool.common.entities.AuditTrail;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface AuditTrailsService {
    Page<AuditTrail> allAuditTrailsWithPagination(int page, int size);
}