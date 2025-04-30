package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.models.SubsidiaryRequest;
import com.kcb.recon.tool.configurations.entities.Subsidiary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SubsidiaryService {
    ResponseMessage createSubsidiary(SubsidiaryRequest request);
    Subsidiary findByCompanyName(String companyName);
    Subsidiary findByCompanyCode(String companyCode);
    List<Subsidiary> allCountriesWithoutPagination();
}
