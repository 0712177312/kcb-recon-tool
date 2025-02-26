package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode,Long> {
    Optional<OtpCode> findByUsernameAndToken(String username, String token);
    Optional<OtpCode> findByUsername(String username);
}
