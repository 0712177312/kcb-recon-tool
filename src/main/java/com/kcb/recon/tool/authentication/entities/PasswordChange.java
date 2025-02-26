package com.kcb.recon.tool.authentication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "password_change_requests")
public class PasswordChange {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "sent_to")
    private String sentTo;
    @Column(name = "date_sent")
    private Date dateSent;
    @Column(name = "date_reset")
    private Date dateReset;
    @Column(name = "status")
    private boolean status;
}
