package com.kcb.recon.tool.authentication.controllers;

import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Users")
@CrossOrigin
public class UsersController {


    private final UsersService usersService;
    private final EncryptionService encryptionService;

    public UsersController(UsersService usersService, EncryptionService encryptionService) {
        this.usersService = usersService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/CreateUserAccount")
    @Operation(summary = "Create User account",description = "Add user details")
    public ResponseEntity<?> AddUserAccount(@RequestBody(required = false) String request,
                                            @RequestParam(defaultValue = "false") boolean encrypted,
                                            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UserAccountRequest accountRequest = encrypted ? encryptionService.decrypt(request, UserAccountRequest.class, key) : new Gson().fromJson(request, UserAccountRequest.class);
            res = usersService.registerUser(accountRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error creating new user account | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/CreateAdminAccount")
    public ResponseEntity<?> AddAdminAccount(@RequestBody(required = false) String request,
                                             @RequestParam(defaultValue = "false") boolean encrypted,
                                             @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UserAccountRequest accountRequest = encrypted ? encryptionService.decrypt(request, UserAccountRequest.class, key) : new Gson().fromJson(request, UserAccountRequest.class);
            res = usersService.registerAdmin(accountRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error creating new admin account | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/CreateSuperAdminAccount")
    public ResponseEntity<?> CreateSuperAdminAccount(@RequestBody(required = false) String request,
                                                     @RequestParam(defaultValue = "false") boolean encrypted,
                                                     @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UserAccountRequest accountRequest = encrypted ? encryptionService.decrypt(request, UserAccountRequest.class, key) : new Gson().fromJson(request, UserAccountRequest.class);
            res = usersService.registerSuperadmin(accountRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error creating new superadmin account | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ActivateDeactivate")
    public ResponseEntity<?> ActivateDeactivate(@RequestBody(required = false) String request,
                                                @RequestParam(defaultValue = "false") boolean encrypted,
                                                @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ActivateDeactivateRequest activateDeactivateRequest = encrypted
                    ? encryptionService.decrypt(request, ActivateDeactivateRequest.class, key)
                    : new Gson().fromJson(request, ActivateDeactivateRequest.class);
            res = usersService.activateDeactivateUserAccount(activateDeactivateRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Error Activating / Deactivating | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/SuperAdminAccounts")
    public ResponseEntity<?> SuperAdminAccounts(@RequestBody(required = false) String request,
                                           @RequestParam(defaultValue = "false") boolean encrypted,
                                           @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            SuperAdminAccountsFilter userAccountsFilter = encrypted ? encryptionService.decrypt(request, SuperAdminAccountsFilter.class, key) : new Gson().fromJson(request, SuperAdminAccountsFilter.class);
            var data = usersService.superAdminAccountsWithPaginationAndUserTypeFilter(userAccountsFilter);
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
            res.setMessage("Error Fetching records | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/AdminAccounts")
    public ResponseEntity<?> AdminAccounts(@RequestBody(required = false) String request,
                                          @RequestParam(defaultValue = "false") boolean encrypted,
                                          @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            AdminAccountsFilter userAccountsFilter = encrypted ? encryptionService.decrypt(request, AdminAccountsFilter.class, key) : new Gson().fromJson(request, AdminAccountsFilter.class);
            var data = usersService.adminAccountsWithPaginationAndUserTypeFilter(userAccountsFilter);
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
            res.setMessage("Error Fetching records | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/UserAccounts")
    public ResponseEntity<?> UserAccounts(@RequestBody(required = false) String request,
                                           @RequestParam(defaultValue = "false") boolean encrypted,
                                           @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UserAccountsFilter userAccountsFilter = encrypted ? encryptionService.decrypt(request, UserAccountsFilter.class, key) : new Gson().fromJson(request, UserAccountsFilter.class);
            var data = usersService.userAccountsWithPaginationAndUserTypeFilter(userAccountsFilter);
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
            res.setMessage("Error Fetching records | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @GetMapping("/Profile")
    public ResponseEntity<?> UserProfileDetails(@RequestParam("id") Long id,
                                                @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var profile = usersService.findByUserId(id);
            String responseBody = encryptionService.encrypt(new Gson().toJson(profile), key);
            HttpStatus status = profile != null ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            var res = new ResponseMessage();
            res.setMessage("Failed to encrypt profile details | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/ProfileUpdate")
    public ResponseEntity<?> UpdateProfileDetails(
            @RequestBody(required = false) String request,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UpdateProfileRequest updateProfileRequest = encrypted ? encryptionService.decrypt(request, UpdateProfileRequest.class, key) : new Gson().fromJson(request, UpdateProfileRequest.class);
            res = usersService.SuperAdminUpdateProfile(updateProfileRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error updating user profile | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/updateUserDetails")
    public ResponseEntity<?> AdminUpdateUserProfile(
            @RequestBody(required = false) String request,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UpdateProfileRequest updateProfileRequest = encrypted ? encryptionService.decrypt(request, UpdateProfileRequest.class, key) : new Gson().fromJson(request, UpdateProfileRequest.class);
            res = usersService.AdminUpdateUserProfile(updateProfileRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error updating user profile | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }
}