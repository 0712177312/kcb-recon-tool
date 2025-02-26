package com.kcb.recon.tool.authentication.services;

import com.kcb.recon.tool.authentication.entities.UserSession;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public interface UserSessionsService {
    void createUserSession(UserSession session);
    void updateUserSession(UserSession session);
    Optional<UserSession> findByIssuedTo(String issuedTo);
    void logout(String username);
}