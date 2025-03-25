package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.PasswordChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordChangeRepository extends JpaRepository<PasswordChange, Long> {
    Optional<PasswordChange> findByUsername(String username);
}
