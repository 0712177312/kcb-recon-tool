package com.kcb.recon.tool.common.controllers;

import com.kcb.recon.tool.common.services.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/Dashboard")
@CrossOrigin
@Slf4j
public class DashboardsController {

    private final  DashboardService dashboardService;

    public DashboardsController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/SuperAdmin")
    public ResponseEntity<?> SuperAdminDashboard()
    {
        log.info("Inside SuperAdminDashboard() At {} ", new Date());
        return new ResponseEntity<>(dashboardService.superAdminDashboardData(), HttpStatus.OK);
    }

    @GetMapping("/Admin")
    public ResponseEntity<?> CountryAdminDashboard()
    {
        return new ResponseEntity<>(dashboardService.adminDashboardData(), HttpStatus.OK);
    }
}
