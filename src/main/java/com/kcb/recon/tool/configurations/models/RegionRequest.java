package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionRequest {
    private String name;
    private Long organization;
    private String user;
    private String code;
    private Long id;
    private boolean status;
}
