package com.kcb.recon.tool.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordsFilter {
    private String status;
    private int page;
    private int size;
}
