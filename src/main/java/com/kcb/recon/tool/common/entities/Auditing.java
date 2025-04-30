package com.kcb.recon.tool.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class Auditing {
    @Column(name = "created_by")
    private String createdBy = "System";
    @Column(name = "created_on")
    private Date createdOn = new Date();
    @Column(name = "modified_on")
    private Date modifiedOn = null;
    @Column(name = "modified_by")
    private String modifiedBy;
    @Column(name = "checked_by")
    private String checkedBy;
    @Column(name = "checked_on")
    private Date checkedOn=null;
    @Column(name="status")
    private String status;
    @Column(name="validity_status")
    private String validityStatus;
    @Column(name="change_status")
    private String changeStatus="NA";
    @Column(name = "remarks")
    @Lob
    private String remarks;
    @Column(name = "changed_values")
    @Lob
    private String changedValues;
    @Column(name = "new_values")
    @Lob
    private String newValues;
}
