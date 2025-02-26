package com.kcb.recon.tool.common.services;

import org.springframework.stereotype.Component;

@Component
public interface ReportsService {
    String generateAccountOpeningForm(Long beneficiaryId,String serialNo);
}
