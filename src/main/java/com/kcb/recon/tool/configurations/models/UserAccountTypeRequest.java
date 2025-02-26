package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountTypeRequest {
    private Long id;
    private String name;
    private boolean enableOtp;
    private String username;
    private boolean status;
}
