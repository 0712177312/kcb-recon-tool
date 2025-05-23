package com.kcb.recon.tool.common.controllers;

import com.kcb.recon.tool.common.services.EncryptionService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security")
public class EncryptionDecryptionController {


    private final EncryptionService encryptionService;

    public EncryptionDecryptionController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @GetMapping("/double/key")
    public ResponseEntity<?> GetAESKey(){
        return new ResponseEntity<>(encryptionService.encryptAESKeyWithRSA(),HttpStatus.OK);
    }

    @PostMapping("/double/encrypt")
    public ResponseEntity<?> DoubleEncrypt(@RequestBody String request,
                                           @RequestHeader("key") String key){

        return new ResponseEntity<>(encryptionService.encrypt(new Gson().toJson(request),key),HttpStatus.OK);
    }

    @PostMapping("/double/decrypt")
    public ResponseEntity<?> DuubleDecrypt(@RequestBody String request,
                                           @RequestHeader("key") String key){
        return new ResponseEntity<>(encryptionService.decrypt(request,Object.class,key),HttpStatus.OK);
    }
}
