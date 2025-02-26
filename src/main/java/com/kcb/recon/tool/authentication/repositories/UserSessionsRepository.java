package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.User;
import com.kcb.recon.tool.authentication.entities.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSessionsRepository extends JpaRepository<UserSession,Long> {
    Optional<UserSession> findByUser(User user);
    Optional<UserSession> findByIssuedTo(String issuedTo);
}