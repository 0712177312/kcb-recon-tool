package com.kcb.recon.tool.authentication.controllers;

import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.authentication.services.UserSessionsService;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.common.models.AuthenticationResponse;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Auth")
@CrossOrigin
@Slf4j
public class AuthenticationController {


    private final UsersService usersService;


    private final UserSessionsService userSessionsService;


    private final EncryptionService encryptionService;

    public AuthenticationController(UsersService usersService, UserSessionsService userSessionsService, EncryptionService encryptionService) {
        this.usersService = usersService;
        this.userSessionsService = userSessionsService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/Authenticate")
    public ResponseEntity<EncryptedResponse> signIn(@RequestBody(required = false) String request,
                                                    @RequestBody(required = false) LoginRequest payload,
                                                    @RequestHeader(value = "key", required = false) String key,
                                                    @RequestParam(required = false, defaultValue = "false") boolean encrypted) {
        new AuthenticationResponse();
        AuthenticationResponse res;
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            LoginRequest loginRequest = encrypted
                    ? encryptionService.decrypt(request, LoginRequest.class, key)
                    : new Gson().fromJson(request, LoginRequest.class);
            log.info("Request {} ",new Gson().toJson(loginRequest));
            res = usersService.login(loginRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(res != null && res.isStatus() ? 200 : 417);
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            res = new AuthenticationResponse();
            res.setMessage("Error processing request | " + e.getMessage());
            res.setStatus(false);
            resBody.setBody(new Gson().toJson(res));
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/RequestPasswordChange")
    public ResponseEntity<EncryptedResponse> requestAdminToResetPassword(@RequestParam("email") String emailAddress,
                                                                         @RequestHeader("key") String key) {
        ResponseMessage res = usersService.sendPasswordResetRequest(emailAddress);
        EncryptedResponse resBody = new EncryptedResponse();
        if (res != null) {
            try {
                String encryptedResponse = encryptionService.encrypt(new Gson().toJson(res), key);
                resBody.setBody(encryptedResponse);
                resBody.setCode(200);
                return new ResponseEntity<>(resBody, HttpStatus.OK);
            } catch (Exception e) {
                resBody.setBody("Error encrypting response: " + e.getMessage());
                resBody.setCode(417);
                return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            resBody.setBody("Failed to send password reset request");
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/RefreshAccessToken")
    public ResponseEntity<?> RefreshAccessToken(@RequestHeader("token") String token) {
        RefreshTokenResponse res = usersService.refreshAccessToken(token);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public void logout(@RequestParam("username") String username) {
        userSessionsService.logout(username);
    }

    @PostMapping("/ResetPassword")
    public ResponseEntity<EncryptedResponse> resetPassword(@RequestBody(required = false) String request,
                                                           @RequestBody(required = false) PasswordResetRequest payload,
                                                           @RequestParam(defaultValue = "false") boolean encrypted,
                                                           @RequestHeader("key") String key) {
        var res = new ResponseMessage();
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            PasswordResetRequest passwordResetRequest = encrypted
                    ? encryptionService.decrypt(request, PasswordResetRequest.class, key)
                    : new Gson().fromJson(request, PasswordResetRequest.class);
            res = usersService.ResetPassword(passwordResetRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(res != null && res.isStatus() ? 200 : 417);
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setStatus(false);
            errorResponse.setMessage("Error resetting user password | " + e.getMessage());
            resBody.setBody(new Gson().toJson(errorResponse));
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/FirstLoginReset")
    public ResponseEntity<EncryptedResponse> FirstLoginPasswordReset(@RequestBody(required = false) String request,
                                                           @RequestBody(required = false) FirstTimePasswordChangeRequest payload,
                                                           @RequestParam(defaultValue = "false") boolean encrypted,
                                                           @RequestHeader("key") String key) {
        var res = new ResponseMessage();
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            FirstTimePasswordChangeRequest firstTimePasswordChangeRequest = encrypted
                    ? encryptionService.decrypt(request, FirstTimePasswordChangeRequest.class, key)
                    : new Gson().fromJson(request, FirstTimePasswordChangeRequest.class);
            res = usersService.firstTimeLoginChangePassword(firstTimePasswordChangeRequest);
            HttpStatus status = (res != null && res.isStatus()) ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED;
            String responseBody = res != null && res.isStatus()
                    ? encryptionService.encrypt(new Gson().toJson(res), key)
                    : new Gson().toJson(res);
            resBody.setBody(responseBody);
            resBody.setCode(res != null && res.isStatus() ? 200 : 417);
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setStatus(false);
            errorResponse.setMessage("Error resetting user password during first login | " + e.getMessage());
            resBody.setBody(new Gson().toJson(errorResponse));
            resBody.setCode(417);
            return new ResponseEntity<>(resBody, HttpStatus.EXPECTATION_FAILED);
        }
    }
}