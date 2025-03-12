package com.kcb.recon.tool.authentication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user_sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Lob
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "issued_on")
    private Date issuedOn = new Date();
    @Column(name = "issued_to")
    private String issuedTo;
    @Column(name = "logged_in")
    private boolean loggedIn;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
//    @Column(name="user_id")
//    private int userId;
}
