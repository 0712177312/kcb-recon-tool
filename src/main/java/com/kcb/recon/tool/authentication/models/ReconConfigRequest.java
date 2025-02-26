package com.kcb.recon.tool.authentication.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Hp
 * @date 2/20/2025
 */

@Setter
@Getter
@ToString
public class ReconConfigRequest {
    private String operationName;
    private String transactionType;
    private String transactionID;
    private String transactionTime;
    private String channelCode;
    private String serviceCode;
    private String processingCode;
    private String userCode;
    private String companyCode;
    private List<Items> items;

}
