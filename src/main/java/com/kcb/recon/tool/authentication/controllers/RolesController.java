package com.kcb.recon.tool.authentication.controllers;

import com.kcb.recon.tool.authentication.models.ApproveRejectRequest;
import com.kcb.recon.tool.authentication.models.RoleRequest;
import com.kcb.recon.tool.authentication.models.RolesFilter;
import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Roles")
@CrossOrigin
public class RolesController {


    private final RolesService rolesService;
    private final EncryptionService encryptionService;

    public RolesController(RolesService rolesService, EncryptionService encryptionService) {
        this.rolesService = rolesService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/FilteredRoles")
    @PreAuthorize("hasAuthority('ROLES_FILTEREDROLES')")
    public ResponseEntity<?> GetRolesWithFilters(@RequestBody(required = false) String request,
                                          @RequestBody(required = false) RolesFilter payload,
                                          @RequestParam(defaultValue = "false") boolean encrypted,
                                          @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RolesFilter rolesFilter = encrypted ? encryptionService.decrypt(request, RolesFilter.class,key) : new Gson().fromJson(request, RolesFilter.class);
            var data = rolesService.paginatedRolesListWithFilters(rolesFilter);
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

    @PostMapping("/AdminRoles")
    @PreAuthorize("hasAuthority('ROLES_ADMINROLES')")
    public ResponseEntity<?> AdminRolesList(@RequestBody(required = false) String request,
                                        @RequestBody(required = false) RolesFilter payload,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RolesFilter rolesFilter = encrypted ? encryptionService.decrypt(request, RolesFilter.class,key) : new Gson().fromJson(request, RolesFilter.class);
            var data = rolesService.paginatedAdminRolesListWithFilters(rolesFilter);
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

    @PostMapping("/ReviewList")
//    @PreAuthorize("hasAuthority('ROLES_REVIEWLIST')")
    public ResponseEntity<?> ReviewList(@RequestBody(required = false) String request,
                                                 @RequestBody(required = false) RolesFilter payload,
                                                 @RequestParam(defaultValue = "false") boolean encrypted,
                                                 @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RolesFilter rolesFilter = encrypted ? encryptionService.decrypt(request, RolesFilter.class,key) : new Gson().fromJson(request, RolesFilter.class);
            var data = rolesService.paginatedRolesListWithFiltersForReviewList(rolesFilter);
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
//    @PreAuthorize("hasAuthority('ROLES_MODIFICATIONSREVIEWLIST')")
    public ResponseEntity<?> ModificationsReviewList(@RequestBody(required = false) String request,
                                        @RequestBody(required = false) RolesFilter payload,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RolesFilter rolesFilter = encrypted ? encryptionService.decrypt(request, RolesFilter.class,key) : new Gson().fromJson(request, RolesFilter.class);
            var data = rolesService.paginatedRolesListWithFiltersForModificationsReviewList(rolesFilter);
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
    @PreAuthorize("hasAuthority('ROLES_FINDBYID')")
    public ResponseEntity<?> RoleDetailsById(@RequestParam("id") Long id,
                                                @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var role = rolesService.findRoleById(id);
            String responseBody = encryptionService.encrypt(new Gson().toJson(role), key);
            HttpStatus status = role != null  ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            var res = new ResponseMessage();
            res.setMessage("Failed to encrypt role details | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/All")
    @PreAuthorize("hasAuthority('ROLES_ALL')")
    public ResponseEntity<?> ViewAllRolesWithoutPagination(@RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var roles = rolesService.allRolesSuperAdmin();
            HttpStatus status = (roles != null && !roles.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (roles != null && !roles.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(roles), key)
                    : new Gson().toJson(roles);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt roles list | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

//    @GetMapping("/AllByOrganization")
//    @PreAuthorize("hasAuthority('ROLES_ALLBYORGANIZATION')")
    public ResponseEntity<?> AllByOrganization(@RequestHeader("key") String key,@RequestParam("id") Long organization) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var roles = rolesService.allRolesAdmin(organization);
            HttpStatus status = (roles != null && !roles.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (roles != null && !roles.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(roles), key)
                    : new Gson().toJson(roles);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt roles list | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/Add")
    @PreAuthorize("hasAuthority('ROLES_ADD')")
    public ResponseEntity<?> AddNewRole(@RequestBody(required = false) String request,
                                        @RequestBody(required = false) RoleRequest payload,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RoleRequest roleRequest = encrypted
                    ? encryptionService.decrypt(request, RoleRequest.class, key)
                    : new Gson().fromJson(request, RoleRequest.class);
            res = rolesService.createRole(roleRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error creating role | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/UpdateAdminRole")
    @PreAuthorize("hasAuthority('ROLES_UPDATEADMINROLE')")
    public ResponseEntity<?> UpdateAdminRole(@RequestBody(required = false) String request,
                                        @RequestBody(required = false) RoleRequest payload,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RoleRequest roleRequest = encrypted
                    ? encryptionService.decrypt(request, RoleRequest.class, key)
                    : new Gson().fromJson(request, RoleRequest.class);
            rolesService.updateNoMakerChecker(roleRequest);
            HttpStatus status = HttpStatus.OK;
            res.setMessage("Processed Successfully!");
            res.setStatus(true);
            String responseBody = encryptionService.encrypt(new Gson().toJson(res), key);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error updating role | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

//    @PutMapping("/Update")
//    @PreAuthorize("hasAuthority('ROLES_UPDATE')")
    public ResponseEntity<?> UpdateRole(@RequestBody(required = false) String request,
                                        @RequestBody(required = false) RoleRequest payload,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RoleRequest roleRequest = encrypted
                    ? encryptionService.decrypt(request, RoleRequest.class, key)
                    : new Gson().fromJson(request, RoleRequest.class);
            res = rolesService.updateRoleWithMakerChecker(roleRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error updating role | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

//    @PostMapping("/ApproveRejectEdit")
//    @PreAuthorize("hasAuthority('ROLES_APPROVEREJECTEDIT')")
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
            res = rolesService.approveRejectRoleModification(approveRejectRequest);
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

//    @PostMapping("/ApproveReject")
//    @PreAuthorize("hasAuthority('ROLES_APPROVEREJECT')")
    public ResponseEntity<?> ApproveReject(@RequestBody(required = false) String request,
                                               @RequestBody(required = false) ApproveRejectRequest payload,
                                               @RequestParam(defaultValue = "false") boolean encrypted,
                                               @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ApproveRejectRequest approveRejectRequest = encrypted
                    ? encryptionService.decrypt(request, ApproveRejectRequest.class, key)
                    : new Gson().fromJson(request, ApproveRejectRequest.class);
            res = rolesService.approveRejectRole(approveRejectRequest);
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
}