package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.PasswordChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordChangeRepository extends JpaRepository<PasswordChange, Long> {
    Optional<PasswordChange> findByUsername(String username);
    @Query(nativeQuery = true, value = "SELECT * FROM password_change_requests WHERE status=0 AND sent_to=:username")
    Page<PasswordChange> fetchBySentToAndStatus(@Param("username") String username, Pageable pageable);
    Page<PasswordChange> findByUsername(String username,Pageable pageable);
}
