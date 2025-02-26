package com.kcb.recon.tool.common.services.impl;

import com.kcb.recon.tool.authentication.services.RolesService;
import com.kcb.recon.tool.authentication.services.UsersService;
import com.kcb.recon.tool.common.services.DashboardService;
import com.kcb.recon.tool.configurations.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private UsersService usersService;
    @Autowired
    private CountriesService countriesService;
    @Autowired

    private RolesService rolesService;


    @Override
    public Map<String, Object> superAdminDashboardData() {
        Map<String,Object> data = new HashMap<>();
        data.put("superAdmins",usersService.superAdminAccountsWithoutPagination().size());
        data.put("countryAdmins",usersService.adminAccountsWithoutPagination().size());
        data.put("countries",countriesService.allCountriesWithoutPagination().size());
        return data;
    }

    @Override
    public Map<String, Object> adminDashboardData() {
        Map<String,Object> data = new HashMap<>();
        data.put("countryUsers",usersService.userAccountsWithoutPaginationPerOrganization().size());
        data.put("countries",countriesService.allCountriesWithoutPagination().size());
        return data;
    }
}
