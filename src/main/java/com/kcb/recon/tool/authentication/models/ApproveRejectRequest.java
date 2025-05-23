package com.kcb.recon.tool.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApproveRejectRequest {
    private Long recordId;
    private String checkerName;
    private String remarks;
    private boolean approve;

}
