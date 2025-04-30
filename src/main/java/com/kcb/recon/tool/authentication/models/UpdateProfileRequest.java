package com.kcb.recon.tool.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String firstName;
    private String otherNames;
    private String modifiedBy;
    private String phoneNumber;
    private String emailAddress;
    private String gender;
    private Long id;
    private String userId;
    private int role;
    private boolean acctStatus;
    private Long country;
    private String companyName;
    private String companyCode;
    List<Long> roles = new ArrayList<>();
}