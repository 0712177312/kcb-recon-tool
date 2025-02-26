package com.kcb.recon.tool.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationAdapterResponse {
    private String message;
    private Object data;
    private boolean status;
}
