package com.kcb.recon.tool.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminChangeUserAccountType {
    private Long userId;
    private String adminName;
    private Long userType;
}
