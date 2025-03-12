package com.kcb.recon.tool.authentication.controllers;

import com.kcb.recon.tool.authentication.models.PermissionRequest;
import com.kcb.recon.tool.authentication.services.PermissionsService;
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
@RequestMapping("/api/v1/Permissions")
@CrossOrigin
public class PermissionsController {


    private final PermissionsService permissionsService;


    private  final EncryptionService encryptionService;

    public PermissionsController(PermissionsService permissionsService, EncryptionService encryptionService) {
        this.permissionsService = permissionsService;
        this.encryptionService = encryptionService;
    }


    @GetMapping("/View")
    @PreAuthorize("hasAuthority('PERMISSIONS_VIEW')")
    public ResponseEntity<?> ViewPermissionsWithPagination(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var permissions = permissionsService.paginatedPermissionsList(page, size);
            HttpStatus status = (permissions != null && !permissions.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (permissions != null && !permissions.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(permissions), key)
                    : new Gson().toJson(permissions);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt permissions list | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/All")
    @PreAuthorize("hasAuthority('PERMISSIONS_ALL')")
    public ResponseEntity<?> ViewPermissionsWithoutPagination(@RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var permissions = permissionsService.allPermissions();
            HttpStatus status = (permissions != null && !permissions.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (permissions != null && !permissions.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(permissions), key)
                    : new Gson().toJson(permissions);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt permissions list | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/Add")
    @PreAuthorize("hasAuthority('PERMISSIONS_ADD')")
    public ResponseEntity<?> AddNewPermission(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) PermissionRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            PermissionRequest permissionRequest = encrypted
                    ? encryptionService.decrypt(request, PermissionRequest.class,key)
                    : new Gson().fromJson(request, PermissionRequest.class);
            res = permissionsService.createPermission(permissionRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error creating permission | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

//    @PutMapping("/Update")
//    @PreAuthorize("hasAuthority('PERMISSIONS_UPDATE')")
    public ResponseEntity<?> UpdatePermissionDetails(@RequestBody(required = false) String request,
                                                     @RequestBody(required = false) PermissionRequest payload,
                                                     @RequestParam(defaultValue = "false") boolean encrypted,
                                                     @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            PermissionRequest permissionRequest = encrypted
                    ? encryptionService.decrypt(request, PermissionRequest.class,key)
                    : new Gson().fromJson(request, PermissionRequest.class);
            res = permissionsService.updatePermission(permissionRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error updating permission | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }
}