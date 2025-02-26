package com.kcb.recon.tool.configurations.controllers;

import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.RecordsFilter;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.kcb.recon.tool.configurations.models.LanguageRequest;
import com.kcb.recon.tool.configurations.services.LanguagesService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Languages")
@CrossOrigin
public class LanguagesController {

    @Autowired
    private LanguagesService languagesService;
    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/View")
    @PreAuthorize("hasAuthority('LANGUAGES_VIEW')")
    public ResponseEntity<?> ViewWithPagination(@RequestBody(required = false) String request,
                                                @RequestBody(required = false) RecordsFilter payload,
                                                @RequestParam(defaultValue = "false") boolean encrypted,
                                                @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RecordsFilter filter = encrypted ? encryptionService.decrypt(request, RecordsFilter.class, key) : new Gson().fromJson(request, RecordsFilter.class);
            var data = languagesService.allWithPagination(filter);
            res.setData(data);
            res.setStatus(true);
            res.setMessage("Successful");
            var encryptedData = encryptionService.encrypt(new Gson().toJson(res), key);
            HttpStatus status = encryptedData != null ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(encryptedData);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error Fetching | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/All")
    @PreAuthorize("hasAuthority('LANGUAGES_ALL')")
    public ResponseEntity<?> ViewWithoutPagination(@RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var partnerTypes = languagesService.allWithoutPagination();
            HttpStatus status = (partnerTypes != null && !partnerTypes.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (partnerTypes != null && !partnerTypes.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(partnerTypes), key)
                    : new Gson().toJson(partnerTypes);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/Add")
    @PreAuthorize("hasAuthority('LANGUAGES_ADD')")
    public ResponseEntity<?> AddNew(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) LanguageRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            LanguageRequest request1 = encrypted
                    ? encryptionService.decrypt(request, LanguageRequest.class, key)
                    : new Gson().fromJson(request, LanguageRequest.class);
            res = languagesService.create(request1);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error creating |  " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/Update")
    @PreAuthorize("hasAuthority('LANGUAGES_UPDATE')")
    public ResponseEntity<?> UpdateDetails(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) LanguageRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            LanguageRequest request1 = encrypted
                    ? encryptionService.decrypt(request, LanguageRequest.class, key)
                    : new Gson().fromJson(request, LanguageRequest.class);
            res = languagesService.update(request1);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error updating | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/FindById")
    @PreAuthorize("hasAuthority('LANGUAGES_FINDBYID')")
    public ResponseEntity<?> FindById(@RequestParam Long id, @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var data = languagesService.findById(id);
            String responseBody = encryptionService.encrypt(new Gson().toJson(data), key);
            HttpStatus status = data != null ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt  | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }
}