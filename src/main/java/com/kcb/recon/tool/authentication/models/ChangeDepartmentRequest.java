package com.kcb.recon.tool.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDepartmentRequest {
    public Long userId;
    public Long departmentId;
    public String modifiedBy;
}
