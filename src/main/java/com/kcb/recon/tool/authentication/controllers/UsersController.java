package com.kcb.recon.tool.authentication.controllers;

import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('USERS_CREATEUSERACCOUNT')")
    public ResponseEntity<?> AddUserAccount(@RequestBody(required = false) String request,
                                            @RequestBody(required = false) UserAccountRequest payload,
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
    @PreAuthorize("hasAuthority('USERS_CREATEADMINACCOUNT')")
    public ResponseEntity<?> AddAdminAccount(@RequestBody(required = false) String request,
                                             @RequestBody(required = false) UserAccountRequest payload,
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
    @PreAuthorize("hasAuthority('USERS_CREATESUPERADMINACCOUNT')")
    public ResponseEntity<?> CreateSuperAdminAccount(@RequestBody(required = false) String request,
                                                     @RequestBody(required = false) UserAccountRequest payload,
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
    @PreAuthorize("hasAuthority('USERS_ACTIVATEDEACTIVATE')")
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
    @PreAuthorize("hasAuthority('USERS_SUPERADMINACCOUNTS')")
    public ResponseEntity<?> SuperAdminAccounts(@RequestBody(required = false) String request,
                                           @RequestBody(required = false) SuperAdminAccountsFilter payload,
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
    @PreAuthorize("hasAuthority('USERS_ADMINACCOUNTS')")
    public ResponseEntity<?> AdminAccounts(@RequestBody(required = false) String request,
                                          @RequestBody(required = false) AdminAccountsFilter payload,
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
    @PreAuthorize("hasAuthority('USERS_USERACCOUNTS')")
    public ResponseEntity<?> UserAccounts(@RequestBody(required = false) String request,
                                           @RequestBody(required = false) UserAccountsFilter payload,
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

    @PostMapping("/UserAccountsReviewList")
    @PreAuthorize("hasAuthority('USERS_USERACCOUNTSREVIEWLIST')")
    public ResponseEntity<?> UserAccountsReviewList(@RequestBody(required = false) String request,
                                          @RequestBody(required = false) UserAccountsFilter payload,
                                          @RequestParam(defaultValue = "false") boolean encrypted,
                                          @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UserAccountsFilter userAccountsFilter = encrypted ? encryptionService.decrypt(request, UserAccountsFilter.class, key) : new Gson().fromJson(request, UserAccountsFilter.class);
            var data = usersService.userAccountsWithPaginationAndUserTypeFilterForReviewList(userAccountsFilter);
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

    @PostMapping("/UserAccountsEditsReviewList")
    @PreAuthorize("hasAuthority('USERS_USERACCOUNTSEDITSREVIEWLIST')")
    public ResponseEntity<?> UserAccountsEditsReviewList(@RequestBody(required = false) String request,
                                                    @RequestBody(required = false) UserAccountsFilter payload,
                                                    @RequestParam(defaultValue = "false") boolean encrypted,
                                                    @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            UserAccountsFilter userAccountsFilter = encrypted ? encryptionService.decrypt(request, UserAccountsFilter.class, key) : new Gson().fromJson(request, UserAccountsFilter.class);
            var data = usersService.userAccountsWithPaginationAndUserTypeFilterForModificationsReviewList(userAccountsFilter);
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

    @PostMapping("/ApproveReject")
    @PreAuthorize("hasAuthority('USERS_APPROVEREJECT')")
    public ResponseEntity<?> ApproveReject(@RequestBody(required = false) String request,
                                            @RequestBody(required = false) ApproveRejectRequest payload,
                                            @RequestParam(defaultValue = "false") boolean encrypted,
                                            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ApproveRejectRequest approveRejectRequest = encrypted ? encryptionService.decrypt(request, ApproveRejectRequest.class, key) : new Gson().fromJson(request, ApproveRejectRequest.class);
            res = usersService.approveRejectUserAccount(approveRejectRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error processing request | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/Profile")
    @PreAuthorize("hasAuthority('USERS_PROFILE')")
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

    @PostMapping("/UserChangePassword")
    @PreAuthorize("hasAuthority('USERS_USERCHANGEPASSWORD')")
    public ResponseEntity<?> UpdatePassword(@RequestBody(required = false) String request,
                                            @RequestBody(required = false) ChangePasswordRequest payload,
                                            @RequestParam(defaultValue = "false") boolean encrypted,
                                            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ChangePasswordRequest passwordRequest = encrypted ? encryptionService.decrypt(request, ChangePasswordRequest.class, key) : new Gson().fromJson(request, ChangePasswordRequest.class);
            res = usersService.UserChangePassword(passwordRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error changing user password | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/AdminChangeUserPassword")
    @PreAuthorize("hasAuthority('USERS_ADMINCHANGEUSERPASSWORD')")
    public ResponseEntity<?> AdminUpdateUserPassword(@RequestBody(required = false) String request,
                                                     @RequestBody(required = false) AdminChangeUserPasswordRequest payload,
                                                     @RequestParam(defaultValue = "false") boolean encrypted,
                                                     @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            AdminChangeUserPasswordRequest passwordRequest = encrypted ? encryptionService.decrypt(request, AdminChangeUserPasswordRequest.class, key) : new Gson().fromJson(request, AdminChangeUserPasswordRequest.class);
            res = usersService.AdminChangeUserPassword(passwordRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error changing user password | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/ProfileUpdate")
    @PreAuthorize("hasAuthority('USERS_PROFILEUPDATE')")
    public ResponseEntity<?> UpdateProfileDetails(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) UpdateProfileRequest payload,
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

    @PutMapping("/AdminUpdateUserProfile")
    @PreAuthorize("hasAuthority('USERS_ADMINUPDATEUSERPROFILE')")
    public ResponseEntity<?> AdminUpdateUserProfile(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) UpdateProfileRequest payload,
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

    @PostMapping("/ApproveRejectEdit")
    @PreAuthorize("hasAuthority('USERS_APPROVEREJECTEDIT')")
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
            res = usersService.approveRejectUserAccountModification(approveRejectRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
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

    @PostMapping("/UserViewPasswordResets")
    @PreAuthorize("hasAuthority('USERS_USERVIEWPASSWORDRESETS')")
    public ResponseEntity<?> UserViewPasswordResets(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) PasswordResetFilter payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            PasswordResetFilter passwordResetFilter = encrypted ? encryptionService.decrypt(request, PasswordResetFilter.class, key) : new Gson().fromJson(request, PasswordResetFilter.class);
            var data = usersService.passwordChangeRequestsByUsername(passwordResetFilter);
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
            res.setMessage("Error Fetching user password resets | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/AdminViewPasswordResets")
    @PreAuthorize("hasAuthority('USERS_ADMINVIEWPASSWORDRESETS')")
    public ResponseEntity<?> AdminViewPasswordResets(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) PasswordResetFilter payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            PasswordResetFilter passwordResetFilter = encrypted ? encryptionService.decrypt(request, PasswordResetFilter.class, key) : new Gson().fromJson(request, PasswordResetFilter.class);
            var data = usersService.passwordChangeRequestsByRecipient(passwordResetFilter);
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
            res.setMessage("Error Fetching user password resets for admin | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ChangeUserBranch")
    @PreAuthorize("hasAuthority('USERS_CHANGEUSERBRANCH')")
    public ResponseEntity<?> ChangeUserBranch(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) ChangeUserBranchRequest payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            ChangeUserBranchRequest changeUserBranchRequest = encrypted ? encryptionService.decrypt(request, ChangeUserBranchRequest.class, key) : new Gson().fromJson(request, ChangeUserBranchRequest.class);
            res = usersService.ChangeUserBranch(changeUserBranchRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error changing user branch | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/ChangeUserType")
    @PreAuthorize("hasAuthority('USERS_CHANGEUSERTYPE')")
    public ResponseEntity<?> ChangeUserType(
            @RequestBody(required = false) String request,
            @RequestBody(required = false) AdminChangeUserAccountType payload,
            @RequestParam(defaultValue = "false") boolean encrypted,
            @RequestHeader("key") String key) {
        EncryptedResponse resBody = new EncryptedResponse();
        var res = new ResponseMessage();
        try {
            AdminChangeUserAccountType changeUserBranchRequest = encrypted ? encryptionService.decrypt(request, AdminChangeUserAccountType.class, key) : new Gson().fromJson(request, AdminChangeUserAccountType.class);
            res = usersService.ChangeUserAccountType(changeUserBranchRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.CREATED : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new ResponseMessage();
            res.setMessage("Error changing user account type | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(res);
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }
}