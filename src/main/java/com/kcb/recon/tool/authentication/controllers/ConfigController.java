package com.kcb.recon.tool.authentication.controllers;
import com.kcb.recon.tool.common.models.ConfigServiceResponse;
import com.kcb.recon.tool.common.services.ConfigurationService;
import com.kcb.recon.tool.common.services.EncryptionService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@CrossOrigin
@Slf4j
public class ConfigController {
    private final ConfigurationService configurationService;
    private final EncryptionService encryptionService;

    @PostMapping(value = "/rest")
   // @PreAuthorize("hasAuthority('CONFIGURATION_REQUESTS')")
    public ResponseEntity<ConfigServiceResponse> processConfigRequest(@RequestBody(required = false) String request,@RequestParam(defaultValue = "false") boolean encrypted, @RequestHeader("key") String key) {
       log.info("Received Post request: {}", request);

        ConfigServiceResponse response;
        if (request != null) {
            response = configurationService.sendToConfigService(request);
            log.info("Response from config service: {}", response);
        } else {
            log.warn("No request body received. Returning bad request.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }

}
