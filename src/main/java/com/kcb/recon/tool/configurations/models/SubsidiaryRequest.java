package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubsidiaryRequest {
    private Long id;
    private String companyCode;
    private String companyName;
    private String status;
    private String createdOn;
    private String createdBy;
    private String userName;
}