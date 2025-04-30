package com.kcb.recon.tool.configurations.controllers;


import com.google.gson.Gson;
import com.kcb.recon.tool.common.models.EncryptedResponse;
import com.kcb.recon.tool.common.models.ResponseMessage;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.kcb.recon.tool.configurations.services.MenusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/Menus")
@Slf4j
public class MenuController {

    private final MenusService menusService;
    private final EncryptionService encryptionService;
    public MenuController(MenusService menusService, EncryptionService encryptionService) {
        this.menusService = menusService;
        this.encryptionService = encryptionService;
    }

    @GetMapping("/All")
    public ResponseEntity<?> getAllMenus(@RequestHeader("key") String key) {
        log.info("Get all menus from Menus");
        EncryptedResponse resBody = new EncryptedResponse();
        try {
            var menus = menusService.findAll();
            log.info("Menus found: " + new Gson().toJson(menus));
            HttpStatus status = (menus != null && !menus.isEmpty()) ? HttpStatus.OK : HttpStatus.NO_CONTENT;
            String responseBody = (menus != null && !menus.isEmpty())
                    ? encryptionService.encrypt(new Gson().toJson(menus), key)
                    : new Gson().toJson(menus);
            resBody.setBody(responseBody);
            resBody.setCode(status.value());
            return new ResponseEntity<>(resBody, status);
        } catch (Exception e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage("Failed to encrypt menus list | " + e.getMessage());
            errorResponse.setStatus(false);
            resBody.setBody(errorResponse);
            resBody.setCode(417);
            return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
