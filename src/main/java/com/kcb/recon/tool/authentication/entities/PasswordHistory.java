package com.kcb.recon.tool.authentication.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "password_history")
public class PasswordHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "password")
    private String password;
    @Column(name = "created_at")
    private Date createdAt = new Date();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
