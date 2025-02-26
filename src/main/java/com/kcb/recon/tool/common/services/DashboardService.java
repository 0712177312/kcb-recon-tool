package com.kcb.recon.tool.common.services;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public interface DashboardService {
    Map<String,Object> superAdminDashboardData();
    Map<String,Object> adminDashboardData();
}
