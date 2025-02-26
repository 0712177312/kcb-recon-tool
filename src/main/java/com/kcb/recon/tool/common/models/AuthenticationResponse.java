package com.kcb.recon.tool.common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String token;
    private String refreshToken;
    private Object entity;
    private Object menus;
    private boolean status;
    private String message="";
    private boolean twoFactor;
    private boolean firstLogin;
}
