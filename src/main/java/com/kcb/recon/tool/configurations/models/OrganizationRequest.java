package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequest {
    private String name;
    private String user;
    private String zipCode;
    private String city;
    private Long country;
    private Long id;
    private boolean status;
}
