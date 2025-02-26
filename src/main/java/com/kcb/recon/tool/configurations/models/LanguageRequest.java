package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRequest {
    private Long id;
    private boolean status;
    private String name;
    private String username;
}
