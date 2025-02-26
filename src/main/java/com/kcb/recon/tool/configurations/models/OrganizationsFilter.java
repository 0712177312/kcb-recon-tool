package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationsFilter {
    private String status;
    private int page;
    private int size;
    private Long country;
}
