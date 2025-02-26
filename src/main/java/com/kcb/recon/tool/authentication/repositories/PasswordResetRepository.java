package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.UserPasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<UserPasswordReset,Long> {
    Optional<UserPasswordReset> findByUsernameAndToken(String username, String token);
    Optional<UserPasswordReset> findByUsername(String username);
}