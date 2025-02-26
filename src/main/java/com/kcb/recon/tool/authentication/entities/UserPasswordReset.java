package com.kcb.recon.tool.authentication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "password_resets")
public class UserPasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "token")
    private String token;
    @Column(name = "status")
    private boolean status;
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
}
