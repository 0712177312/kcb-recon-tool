package com.kcb.recon.tool.authentication.controllers;

import com.kcb.recon.tool.authentication.models.*;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.common.models.AuthenticationResponse;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/Auth")
@Slf4j
public class AuthenticationController {


    private final UsersService usersService;

    private final EncryptionService encryptionService;

    public AuthenticationController(UsersService usersService, EncryptionService encryptionService) {
        this.usersService = usersService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/Authenticate")
    public ResponseEntity<EncryptedResponse> signIn(@RequestBody(required = false) String request,
                                                    @RequestHeader(value = "key", required = false) String key,
                                                    @RequestParam(required = false, defaultValue = "false") boolean encrypted) {
        new AuthenticationResponse();
        AuthenticationResponse res;
        EncryptedResponse resBody = new EncryptedResponse();
        try {

            log.info("login request  | {}",request);
            LoginRequest loginRequest = encrypted
                    ? encryptionService.decrypt(request, LoginRequest.class, key)
                    : new Gson().fromJson(request, LoginRequest.class);
            log.info("Logged in User user-name {}  -> ", new Gson().toJson(loginRequest));
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

    @GetMapping("/logout")
    public void logout(@RequestParam("username") String username) {
        usersService.logout(username);
    }

}