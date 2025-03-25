package com.kcb.recon.tool.authentication.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AdResponse {
    private String message;
    private int code;
}
