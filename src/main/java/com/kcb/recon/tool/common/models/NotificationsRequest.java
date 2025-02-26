package com.kcb.recon.tool.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsRequest {
    private String emailRecipient= "-";
    private String smsRecipient= "-";
    private String subject= "-";
    private String emailBody= "-";
    private String smsBody = "-";
    private boolean attach = false;
    private boolean fromThirdPartyAPI = false;
    private byte[] attachment;
}
