package com.kcb.recon.tool.authentication.entities;

import com.kcb.recon.tool.common.entities.Auditing;
import com.kcb.recon.tool.configurations.entities.Country;
import com.kcb.recon.tool.configurations.entities.UserAccountType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user_accounts")
public class User extends Auditing implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "other_names")
    private String otherNames;
    @Column(name = "email_address")
    private String emailAddress;
    @Column(name = "gender")
    private String gender;
    @Column(name = "is_admin")
    private boolean isAdmin;
    @Column(name = "plain_password")
    private String plainPassword;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    @JsonIgnore
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "account_type")
    private UserAccountType accountType;

    @Column(name = "first_time_login")
    private boolean firstTimeLogin = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        roles.stream()
                .flatMap(role -> role.getPermissions().stream()
                        .map(privilege -> new SimpleGrantedAuthority(privilege.getName())))
                .forEach(authorities::add);
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}