package com.kcb.recon.tool.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CombinedIPRSResponse {
    private Long beneficiaryId;
    private String birthDate;
    private String deathDate;
    private String ethnicGroup;
    private String family;
    private String fingerPrint;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String identityDocumentNumber;
    private String photo;
    private String pin;
    private String placeOfBirth;
    private String placeOfDeath;
    private String placeOfLiving;
    private String signature;
    private String nationality;
    private String occupation;
    private String identityDocumentType;
    private String issueDate;
    private String identityDocumentExpiryDate;
    private String issuingAuthority;
    private String serialNumber;
    private String issuingLocation;
    private String responseMessage;
    private String responseCode;
}