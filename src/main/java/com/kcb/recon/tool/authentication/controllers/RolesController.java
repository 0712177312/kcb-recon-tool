package com.kcb.recon.tool.authentication.controllers;

import com.kcb.recon.tool.authentication.models.RoleRequest;
import com.kcb.recon.tool.authentication.models.RolesFilter;
import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.authentication.utils.AppUtillities;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Roles")
@CrossOrigin
@Slf4j
public class RolesController {


    private final RolesService rolesService;
    private final EncryptionService encryptionService;

    public RolesController(RolesService rolesService, EncryptionService encryptionService) {
        this.rolesService = rolesService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/FilteredRoles")
    public ResponseEntity<?> GetRolesWithFilters(

                                                 @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            var data = rolesService.paginatedRolesListWithFilters();
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
            res.setMessage("Error Fetching roles | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/AdminRoles")
    public ResponseEntity<?> AdminRolesList(@RequestBody(required = false) String request,
                                            @RequestParam(defaultValue = "false") boolean encrypted,
                                            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RolesFilter rolesFilter = encrypted ? encryptionService.decrypt(request, RolesFilter.class, key) : new Gson().fromJson(request, RolesFilter.class);

            log.info("role_pagination_request :{}", encryptionService.decrypt(request, RolesFilter.class, key));
            var data = rolesService.paginatedAdminRolesListWithFilters(rolesFilter);
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
            res.setMessage("Error Fetching roles | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @GetMapping("/FindById")
    public ResponseEntity<?> RoleDetailsById(@RequestParam("id") Long id,
                                             @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var role = rolesService.getRoleDetailsWithMenus(id);
            String responseBody = encryptionService.encrypt(new Gson().toJson(role), key);
            HttpStatus status = role != null ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage()
                    + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
            log.error("log-message -> {}", logMessage);
            var res = new ResponseMessage();
            res.setMessage("Failed to encrypt role details | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }



    @GetMapping("/All")
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


    @PostMapping("/Add")
    public ResponseEntity<?> AddNewRole(@RequestBody(required = false) String request,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RoleRequest roleRequest = encrypted
                    ? encryptionService.decrypt(request, RoleRequest.class, key)
                    : new Gson().fromJson(request, RoleRequest.class);
            log.info("role request | {}",roleRequest);
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
    public ResponseEntity<?> UpdateAdminRole(@RequestBody(required = false) String request,
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

     @PostMapping("/Update")
    public ResponseEntity<?> UpdateRole(@RequestBody(required = false) String request,
                                        @RequestParam(defaultValue = "false") boolean encrypted,
                                        @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            RoleRequest roleRequest = encrypted
                    ? encryptionService.decrypt(request, RoleRequest.class, key)
                    : new Gson().fromJson(request, RoleRequest.class);
            res = rolesService.updateRole(roleRequest);
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

}