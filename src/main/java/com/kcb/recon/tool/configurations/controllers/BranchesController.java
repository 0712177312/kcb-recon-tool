package com.kcb.recon.tool.configurations.controllers;

import com.kcb.recon.tool.authentication.models.ApproveRejectRequest;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.kcb.recon.tool.configurations.models.BranchRequest;
import com.kcb.recon.tool.configurations.models.BranchesFilter;
import com.kcb.recon.tool.configurations.services.BranchesService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Branches")
@CrossOrigin
public class BranchesController {
    @Autowired
    private BranchesService branchesService;
    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/Filtered")
    @PreAuthorize("hasAuthority('BRANCHES_FILTERED')")
    public ResponseEntity<?> GetBranchesWithFilters(@RequestBody(required = false) String request,
                                                 @RequestBody(required = false) BranchesFilter payload,
                                                 @RequestParam(defaultValue = "false") boolean encrypted,
                                                 @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            BranchesFilter filter = encrypted ? encryptionService.decrypt(request, BranchesFilter.class,key) : new Gson().fromJson(request, BranchesFilter.class);
            var data = branchesService.paginatedBranchesListWithFilters(filter);
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
            res.setMessage("Error Fetching roles | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/FindById")
    @PreAuthorize("hasAuthority('BRANCHES_FINDBYID')")
    public ResponseEntity<?> BranchDetailsById(@RequestParam("id") Long id,
                                             @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var data = branchesService.findById(id);
            if (data.isPresent()) {
                String responseBody = encryptionService.encrypt(new Gson().toJson(data.get()), key);
                resBody.setBody(responseBody);
                resBody.setCode(HttpStatus.OK.value());
                return new ResponseEntity<>(resBody, HttpStatus.OK);
            } else {
                resBody.setBody("No data found");
                resBody.setCode(HttpStatus.EXPECTATION_FAILED.value());
                return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            var res = new ResponseMessage();
            res.setMessage("Failed to encrypt details | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/FindByOrg")
    public ResponseEntity<?> BranchesPerOrg(@RequestParam("id") Long id,
                                               @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var data = branchesService.allBranchesWithoutPaginationPerOrganizationNoFilters(id);
            if (!data.isEmpty()) {
                String responseBody = encryptionService.encrypt(new Gson().toJson(data), key);
                resBody.setBody(responseBody);
                resBody.setCode(HttpStatus.OK.value());
                return new ResponseEntity<>(resBody, HttpStatus.OK);
            } else {
                resBody.setBody("No data found");
                resBody.setCode(HttpStatus.EXPECTATION_FAILED.value());
                return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            var res = new ResponseMessage();
            res.setMessage("Failed to encrypt details | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/FindByRegion")
    @PreAuthorize("hasAuthority('BRANCHES_FINDBYREGION')")
    public ResponseEntity<?> BranchesByRegion(@RequestParam("id") Long id,
                                               @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var data = branchesService.findBranchesByRegionId(id);
            String responseBody = encryptionService.encrypt(new Gson().toJson(data), key);
            HttpStatus status = data != null ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            var res = new ResponseMessage();
            res.setMessage("Failed to encrypt details | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ApproveRejectEdit")
    @PreAuthorize("hasAuthority('BRANCHES_APPROVEREJECTEDIT')")
    public ResponseEntity<?> ApproveRejectEdit(@RequestBody(required = false) String request,
                                               @RequestBody(required = false) ApproveRejectRequest payload,
                                               @RequestParam(defaultValue = "false") boolean encrypted,
                                               @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ApproveRejectRequest approveRejectRequest = encrypted
                    ? encryptionService.decrypt(request, ApproveRejectRequest.class, key)
                    : new Gson().fromJson(request, ApproveRejectRequest.class);
            res = branchesService.approveRejectBranchModifications(approveRejectRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error processing request | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ReviewList")
    @PreAuthorize("hasAuthority('BRANCHES_REVIEWLIST')")
    public ResponseEntity<?> ReviewList(@RequestBody(required = false) String request,
                                        @RequestBody(required = false) BranchesFilter payload,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            BranchesFilter filter = encrypted ? encryptionService.decrypt(request, BranchesFilter.class,key) : new Gson().fromJson(request, BranchesFilter.class);
            var data = branchesService.paginatedBranchesListWithFiltersForReviewList(filter);
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
            res.setMessage("Error Fetching roles | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ModificationsReviewList")
    @PreAuthorize("hasAuthority('BRANCHES_MODIFICATIONSREVIEWLIST')")
    public ResponseEntity<?> ModificationsReviewList(@RequestBody(required = false) String request,
                                        @RequestBody(required = false) BranchesFilter payload,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            BranchesFilter filter = encrypted ? encryptionService.decrypt(request, BranchesFilter.class,key) : new Gson().fromJson(request, BranchesFilter.class);
            var data = branchesService.paginatedBranchesListWithFiltersForModificationsReviewList(filter);
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
            res.setMessage("Error Fetching roles | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ApproveReject")
    @PreAuthorize("hasAuthority('BRANCHES_APPROVEREJECT')")
    public ResponseEntity<?> ApproveReject(@RequestBody(required = false) String request,
                                                @RequestBody(required = false) ApproveRejectRequest payload,
                                                @RequestParam(defaultValue = "false") boolean encrypted,
                                                @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ApproveRejectRequest activateDeactivateRequest = encrypted
                    ? encryptionService.decrypt(request, ApproveRejectRequest.class, key)
                    : new Gson().fromJson(request, ApproveRejectRequest.class);
            res = branchesService.approveRejectBranch(activateDeactivateRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error Processing | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/All")
    @PreAuthorize("hasAuthority('BRANCHES_ALL')")
    public ResponseEntity<?> ViewBranchesWithoutPagination(@RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var branches = branchesService.allBranchesWithoutPagination();
            HttpStatus status = (branches != null && !branches.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (branches != null && !branches.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(branches), key)
                    : new Gson().toJson(branches);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt branches list | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/Add")
    @PreAuthorize("hasAuthority('BRANCHES_ADD')")
    public ResponseEntity<?> AddNewBranch(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) BranchRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            BranchRequest branchRequest = encrypted
                    ? encryptionService.decrypt(request, BranchRequest.class,key)
                    : new Gson().fromJson(request, BranchRequest.class);
            res = branchesService.createBranch(branchRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error creating new branch | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/Update")
    @PreAuthorize("hasAuthority('BRANCHES_UPDATE')")
    public ResponseEntity<?> UpdateBranchDetails(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) BranchRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            BranchRequest branchRequest = encrypted
                    ? encryptionService.decrypt(request, BranchRequest.class,key)
                    : new Gson().fromJson(request, BranchRequest.class);
            res = branchesService.updateBranch(branchRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error updating branch details | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }
}