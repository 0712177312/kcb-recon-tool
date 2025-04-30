package com.kcb.recon.tool.configurations.services.impl;

import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.models.SubsidiaryRequest;
import com.kcb.recon.tool.configurations.entities.Subsidiary;
import com.kcb.recon.tool.configurations.repositories.InitializeRepository;
import com.kcb.recon.tool.configurations.repositories.CommonRepository;
import com.kcb.recon.tool.configurations.services.SubsidiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SubsidiaryServiceImpl implements SubsidiaryService {

    private final CommonRepository subsidiaryRepository;
    private final InitializeRepository initializeRepository;

    public SubsidiaryServiceImpl(CommonRepository subsidiaryRepository, InitializeRepository initializeRepository) {
        this.subsidiaryRepository = subsidiaryRepository;
        this.initializeRepository = initializeRepository;
    }


    @Override
    public Subsidiary findByCompanyName(String companyName) {
        return subsidiaryRepository.findByCompanyName(companyName);
    }

    @Override
    public Subsidiary findByCompanyCode(String companyCode) {
        return subsidiaryRepository.findByCompanyCode(companyCode);
    }

    @Override
    public List<Subsidiary> allCountriesWithoutPagination() {
        return subsidiaryRepository.allWithoutPagination();
    }

    @Override
    public ResponseMessage createSubsidiary(SubsidiaryRequest request) {
        log.info("Inside createCountry(CountryRequest request) At {} ", new Date());
        var res = new ResponseMessage();
        var subsidiary = new Subsidiary();
        var exists = subsidiaryRepository.findByName(request.getCompanyName());
        if (exists.isPresent()) {
            log.warn("Failed to create Country ! Country {} Already exists!", request.getCompanyName());
            res.setMessage("Country " + request.getCompanyName() + " Already exists!");
            res.setStatus(false);
        } else {
            subsidiary.setCreatedBy(request.getUserName());
            subsidiary.setCompanyName(request.getCompanyName());
            subsidiary.setCompanyCode(request.getCompanyCode());
            subsidiary.setStatus(RecordStatus.Active.name());
            initializeRepository.save(subsidiary);
            res.setStatus(true);
            res.setData(null);
            log.info("Country {} ", request.getCompanyName() + " created successfully!");
            res.setMessage("Created Successfully!");
        }
        return res;
    }

}
