package com.kcb.recon.tool.configurations.services.impl;

import com.kcb.recon.tool.authentication.models.ActivateDeactivateRequest;
import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.Country;
import com.kcb.recon.tool.configurations.models.CountriesFilter;
import com.kcb.recon.tool.configurations.models.CountryRequest;
import com.kcb.recon.tool.configurations.repositories.CountriesRepository;
import com.kcb.recon.tool.configurations.services.CountriesService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CountriesServiceImpl implements CountriesService {
    @Autowired
    private CountriesRepository countriesRepository;

    @Override
    public ResponseMessage createCountry(CountryRequest request) {
        log.info("Inside createCountry(CountryRequest request) At {} ", new Date());
        log.info("Create Country Request {} ",new Gson().toJson(request));
        var res = new ResponseMessage();
        var country = new Country();
        var exists = countriesRepository.findByName(request.getName());
        if (exists.isPresent()) {
            log.warn("Failed to create Country ! Country {} Already exists!", request.getName());
            res.setMessage("Country " + request.getName() + " Already exists!");
            res.setStatus(false);
        } else {
            country.setCreatedBy(request.getUserName());
            country.setName(request.getName());
            country.setCode(request.getCode());
            country.setStatus(RecordStatus.Active.name());
            country.setValidityStatus(ValidityStatus.Approved.name());
            countriesRepository.save(country);
            res.setStatus(true);
            res.setData(null);
            log.info("Country {} ",request.getName()+" created successfully!");
            res.setMessage("Created Successfully!");
        }
        return res;
    }

    @Override
    public ResponseMessage updateCountry(CountryRequest request) {
        log.info("Inside updateCountry(CountryRequest request) {}", new Date());
        log.info("Update Country Request {} ",new Gson().toJson(request));
        var res = new ResponseMessage();
        var exists = countriesRepository.findById(request.getId());
        if (exists.isPresent()) {
            var country = exists.get();
            country.setName(request.getName());
            country.setCode(request.getCode());
            country.setModifiedBy(request.getUserName());
            country.setModifiedOn(new Date());
            country.setChangeStatus(ChangeStatus.Approved.name());
            country.setNewValues(new Gson().toJson(request));
            if(request.isStatus()) {
                country.setStatus(RecordStatus.Active.name());
            }
            else{
                country.setStatus(RecordStatus.Inactive.name());
            }
            countriesRepository.save(country);
            res.setStatus(true);
            res.setData(null);
            log.info("Country {} updated successfully!", request.getName());
            res.setMessage("Updated Successfully!");
        } else {
            res.setMessage("Country Does not exist!");
            res.setStatus(false);
            log.warn("Failed to update Country {} | Country does not exist!", request.getName());
        }
        return res;
    }

    @Override
    public Optional<Country> findCountryById(Long id) {
        log.info("Inside findCountryById(Long id) {} ",new Date());
        log.info("Fetching country details by country id");
        return countriesRepository.findById(id);
    }

    @Override
    public Country findById(Long id) {
        log.info("Inside findById(Long id) {} ", new Date());
        log.info("Fetching country details by country id Country findById(Long id)");
        return countriesRepository.findById(id).orElse(null);
    }


    @Override
    public Optional<Country> findCountryByCountryName(String name) {
        log.info("Inside findCountryByCountryName(String name) {} ",new Date());
        log.info("Fetching country details by country name");
        return countriesRepository.findByName(name);
    }

    @Override
    public List<Country> allCountriesWithoutPagination() {
        log.info("Inside allCountriesWithoutPagination() At {} ",new Date());
        log.info("Fetch all countries without pagination");
        return countriesRepository.allWithoutPagination();
    }

    @Override
    public Page<Country> allCountriesWithFilters(CountriesFilter request) {
        log.info("Inside allCountriesWithFilters(CountriesFilter request) At -> {} ", new Date());
        log.info("Fetch countries with pagination by status {} ", new Gson().toJson(request));

        String status = request.getStatus();
        if(status == null || status.isEmpty()){
            return countriesRepository.allWithPagination(PageRequest.of(request.getPage(), request.getSize()));
        }
        else {
            return countriesRepository.allByStatusWithPagination(status, PageRequest.of(request.getPage(), request.getSize()));
        }
    }

    @Override
    public ResponseMessage activateDeactivateCountry(ActivateDeactivateRequest request) {
        log.info("Inside activateDeactivateCountry(ActivateDeactivateRequest request) At {}", new Date());
        log.info("Request to Activate / Deactivate Country (Organization) | {} ",new Gson().toJson(request));

        var res = new ResponseMessage();
        var exists = countriesRepository.findById(request.getId());
        if (exists.isPresent()) {
            var country = exists.get();
            country.setModifiedBy(request.getUserName());
            country.setModifiedOn(new Date());
            if(request.getAction().equalsIgnoreCase("Activate")) {
                country.setValidityStatus(ValidityStatus.Approved.name());
                country.setStatus(RecordStatus.Active.name());
                res.setStatus(true);
                log.info("Country Activated Successfully!");
                res.setMessage("Activated Successfully!");
            }
            else{
                country.setValidityStatus(ValidityStatus.Disapproved.name());
                country.setStatus(RecordStatus.Inactive.name());
                res.setStatus(true);
                log.info("Country Deactivated Successfully!");
                res.setMessage("Deactivated Successfully!");
            }
            countriesRepository.save(country);
        } else {
            log.warn("Failed to Activate/Deactivate Country | It does not exist!");
            res.setMessage("Country Does not exist!");
            res.setStatus(false);
        }
        return res;
    }
}