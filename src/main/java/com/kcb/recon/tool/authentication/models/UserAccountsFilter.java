package com.kcb.recon.tool.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountsFilter {
    private String status;
    private int page;
    private int size;
    private Long accountType;
    private Long branch;
    private Long organization;
}
