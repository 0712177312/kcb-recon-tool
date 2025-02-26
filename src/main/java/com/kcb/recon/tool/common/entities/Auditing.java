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
    //User who created the record
    @Column(name = "created_by")
    private String createdBy = "System";
    //Date the record was created
    @Column(name = "created_on")
    private Date createdOn = new Date();
    //Date the record was modified
    @Column(name = "modified_on")
    private Date modifiedOn = null;
    //User modifying the record
    @Column(name = "modified_by")
    private String modifiedBy;
    //User approving/rejecting the record after creation


    @Column(name = "checked_by")
    private String checkedBy;
    //Date record was approved/rejected
    @Column(name = "checked_on")
    private Date checkedOn=null;
    //User approving/rejecting record modifications
    @Column(name = "modifications_checked_by")
    private String modificationsCheckedBy;
    //Date record modifications were approved/rejected
    @Column(name = "modifications_checked_on")
    private Date modificationsCheckedOn=null;
    //Status of a record (Inactive/Active) from RecordStatus enum
    @Column(name="status")
    private String status;
    //Validity Status of a record -- shows whether a record is approved/disapproved from (ValidityStatus Enum)
    @Column(name="validity_status")
    private String validityStatus;
    //Edit Status of a record -- shows whether a record is approved/disapproved from (ChangeStatus enum)
    @Column(name="change_status")
    private String changeStatus="NA";
    //Remarks / Comments when approving/rejecting a record
    @Column(name = "remarks")
    @Lob
    private String remarks;
    //Stores json of how a record will be after modification is approved (stores a new record)
    @Column(name = "changed_values")
    @Lob
    private String changedValues;
    //Stores json of request for updating a record e.g. BranchRequest
    @Column(name = "new_values")
    @Lob
    private String newValues;
}
