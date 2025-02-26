package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchRequest {
    private String name;
    private String code;
    private String user;
    private Long id;
    private Long region;
    private boolean status;
}
