package com.kcb.recon.tool.common.controllers;

import com.kcb.recon.tool.common.services.AuditTrailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/AuditTrails")
@CrossOrigin
public class AuditTrailsController {

    private final AuditTrailsService auditTrailsService;

    public AuditTrailsController(AuditTrailsService auditTrailsService) {
        this.auditTrailsService = auditTrailsService;
    }

//    @GetMapping("/View")
//    public ResponseEntity<?> ViewAllLogs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
//        return new ResponseEntity<>(auditTrailsService.allAuditTrailsWithPagination(page, size), HttpStatus.OK);
//    }
}