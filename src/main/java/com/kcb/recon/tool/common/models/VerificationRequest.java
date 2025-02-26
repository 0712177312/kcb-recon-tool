package com.kcb.recon.tool.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationRequest {
    private Long beneficiaryId;
    @SerializedName("documentNumber")
    private String documentNumber;
    @SerializedName("documentType")
    private String documentType;
    @SerializedName("countryCode")
    private String countryCode;
}