package com.kcb.recon.tool.common.models;

import com.kcb.recon.tool.common.enums.ChangeStatus;
import com.kcb.recon.tool.common.enums.RecordStatus;
import com.kcb.recon.tool.common.enums.ValidityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {
    private String message;
    private Object data;
    private boolean status;
    private Object statuses = Arrays.asList(RecordStatus.values());
    private Object validityStatuses = Arrays.asList(ValidityStatus.values());
    private Object changeStatuses = Arrays.asList(ChangeStatus.values());
}