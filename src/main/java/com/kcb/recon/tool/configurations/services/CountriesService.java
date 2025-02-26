package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.authentication.models.ActivateDeactivateRequest;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.configurations.entities.Country;
import com.kcb.recon.tool.configurations.models.CountriesFilter;
import com.kcb.recon.tool.configurations.models.CountryRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public interface CountriesService {
    ResponseMessage createCountry(CountryRequest request);
    ResponseMessage updateCountry(CountryRequest request);
    Optional<Country> findCountryById(Long id);
    Country findById(Long id);
    Optional<Country> findCountryByCountryName(String name);
    List<Country> allCountriesWithoutPagination();
    Page<Country> allCountriesWithFilters(CountriesFilter request);
    ResponseMessage activateDeactivateCountry(ActivateDeactivateRequest request);
}