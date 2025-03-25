package com.kcb.recon.tool.authentication.controllers;

import com.google.gson.Gson;
import com.kcb.recon.tool.common.models.ConfigServiceResponse;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.services.ConfigurationService;
import com.kcb.recon.tool.common.services.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Hp
 * @date 2/20/2025
 */

@RestController
@RequestMapping("/api/v1/config")
@CrossOrigin
@Slf4j
public class ConfigController {
    private final ConfigurationService configurationService;
    private final EncryptionService encryptionService;

    public ConfigController(ConfigurationService configurationService, EncryptionService encryptionService) {
        this.configurationService = configurationService;
        this.encryptionService = encryptionService;
    }

    @PostMapping(value = "/rest")
    public ResponseEntity<EncryptedResponse> processConfigRequest(@RequestBody(required = false) String request,
                                                                      @RequestParam(defaultValue = "false") boolean encrypted,
                                                                      @RequestHeader("key") String key) {
        log.info("Received Post request: {}", request);
        String decrypted = encrypted ? encryptionService.decrypt(request, key) : request;
        log.info("Decrypted: {}", decrypted);
        new ConfigServiceResponse();
        ConfigServiceResponse res;
        EncryptedResponse resBody = new EncryptedResponse();
        res = configurationService.sendToConfigService(decrypted);
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
}
