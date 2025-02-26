package com.kcb.recon.tool.common.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "audit_trails")
public class AuditTrail extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "remote_user")
    private String remoteUser;
    @Column(name = "request_ip")
    private String requestIp;
    @Column(name = "remote_url")
    private String remoteUrl;
    @Column(name = "request_Addr")
    private String requestAddr;
    @Column(name = "remote_port")
    private int remotePort;
    @Column(name = "request_method")
    private String requestMethod;
    @Column(name = "request_parameters")
    @Lob
    private String requestParameters;
    @Column(name = "action")
    private String action;
}