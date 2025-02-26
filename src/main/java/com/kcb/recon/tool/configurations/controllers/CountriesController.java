package com.kcb.recon.tool.configurations.controllers;

import com.kcb.recon.tool.authentication.models.ActivateDeactivateRequest;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.kcb.recon.tool.configurations.models.CountriesFilter;
import com.kcb.recon.tool.configurations.models.CountryRequest;
import com.kcb.recon.tool.configurations.services.CountriesService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Countries")
@CrossOrigin
public class CountriesController {

    @Autowired
    private CountriesService countriesService;
    @Autowired
    private EncryptionService encryptionService;


    @PostMapping("/FilteredCountries")
    @PreAuthorize("hasAuthority('COUNTRIES_FILTEREDCOUNTRIES')")
    public ResponseEntity<?> GetCountriesWithFilters(@RequestBody(required = false) String request,
                                                 @RequestBody(required = false) CountriesFilter payload,
                                                 @RequestParam(defaultValue = "false") boolean encrypted,
                                                 @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            CountriesFilter countriesFilter = encrypted ? encryptionService.decrypt(request, CountriesFilter.class,key) : new Gson().fromJson(request, CountriesFilter.class);
            var data = countriesService.allCountriesWithFilters(countriesFilter);
            res.setData(data);
            res.setStatus(true);
            res.setMessage("Successful");
            var encryptedData = encryptionService.encrypt(new Gson().toJson(res),key);
            HttpStatus status = encryptedData != null  ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(encryptedData);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error Fetching Countries | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ActivateDeactivate")
    @PreAuthorize("hasAuthority('COUNTRIES_ACTIVATEDEACTIVATE')")
    public ResponseEntity<?> ActivateDeactivate(@RequestBody(required = false) String request,
                                                @RequestBody(required = false) ActivateDeactivateRequest payload,
                                                @RequestParam(defaultValue = "false") boolean encrypted,
                                                @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ActivateDeactivateRequest activateDeactivateRequest = encrypted
                    ? encryptionService.decrypt(request, ActivateDeactivateRequest.class, key)
                    : new Gson().fromJson(request, ActivateDeactivateRequest.class);
            res = countriesService.activateDeactivateCountry(activateDeactivateRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error Processing Request | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/FindById")
    @PreAuthorize("hasAuthority('COUNTRIES_FINDBYID')")
    public ResponseEntity<?> FindById(@RequestParam Long id, @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var country = countriesService.findById(id);
            String responseBody = encryptionService.encrypt(new Gson().toJson(country), key);
            HttpStatus status = country != null ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt country details | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/All")
    @PreAuthorize("hasAuthority('COUNTRIES_ALL')")
    public ResponseEntity<?> ViewCountriesWithoutPagination(@RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var countries = countriesService.allCountriesWithoutPagination();
            HttpStatus status = (countries != null && !countries.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (countries != null && !countries.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(countries), key)
                    : new Gson().toJson(countries);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt countries list | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PreAuthorize("hasAuthority('COUNTRIES_ADD')")
    @PostMapping("/Add")
    public ResponseEntity<?> AddNewCountry(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) CountryRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            CountryRequest countryRequest = encrypted
                    ? encryptionService.decrypt(request, CountryRequest.class, key)
                    : new Gson().fromJson(request, CountryRequest.class);
            res = countriesService.createCountry(countryRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error creating new country | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/Update")
    @PreAuthorize("hasAuthority('COUNTRIES_UPDATE')")
    public ResponseEntity<?> UpdateCountryDetails(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) CountryRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            CountryRequest countryRequest = encrypted
                    ? encryptionService.decrypt(request, CountryRequest.class, key)
                    : new Gson().fromJson(request, CountryRequest.class);
            res = countriesService.updateCountry(countryRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error updating country details | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }
}