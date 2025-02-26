package com.kcb.recon.tool.common.services.impl;

import com.kcb.recon.tool.common.entities.AuditTrail;
import com.kcb.recon.tool.common.repositories.AuditTrailsRepository;
import com.kcb.recon.tool.common.services.AuditTrailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditTrailsServiceImpl implements AuditTrailsService {
    @Autowired
    private AuditTrailsRepository auditTrailsRepository;

    @Override
    public Page<AuditTrail> allAuditTrailsWithPagination(int page, int size) {
        return auditTrailsRepository.allAuditTrails(PageRequest.of(page, size));
    }
}
