package com.kcb.recon.tool.configurations.entities;



import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="CONFIG_COMPANY_CODE")
public class Subsidiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String companyCode;
    private String companyName;
    private String status;
    private String createdOn;
    private String createdBy;

}
