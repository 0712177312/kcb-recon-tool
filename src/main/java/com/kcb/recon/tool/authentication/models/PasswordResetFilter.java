package com.kcb.recon.tool.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetFilter {
    private String username;
    private String status;
    private int page;
    private int size;
}
