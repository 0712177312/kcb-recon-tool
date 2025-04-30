package com.kcb.recon.tool.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountRequest {
    private String firstName;
    private String otherNames;
    private String emailAddress;
    private String username;
    private String phoneNumber;
    private String gender;
    private boolean isAdmin;
    private String userId;
    private String adminName;
    private Long organization;
    private String companyName;
    private String companyCode;
    private String country;
    private Long branch;
    List<Long> roles = new ArrayList<>();
}
