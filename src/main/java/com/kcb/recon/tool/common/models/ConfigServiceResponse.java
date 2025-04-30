package com.kcb.recon.tool.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hp
 * @date 2/20/2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigServiceResponse {
    private String message;
    private Object data;
    private boolean status;
    private int code;
}
