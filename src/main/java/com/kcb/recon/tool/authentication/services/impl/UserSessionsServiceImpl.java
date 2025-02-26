package com.kcb.recon.tool.authentication.services.impl;

import com.kcb.recon.tool.authentication.entities.UserSession;
import com.kcb.recon.tool.authentication.repositories.UserSessionsRepository;
import com.kcb.recon.tool.authentication.services.UserSessionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UserSessionsServiceImpl implements UserSessionsService {

    @Autowired
    private UserSessionsRepository userSessionsRepository;

    @Override
    public void createUserSession(UserSession session) {
        log.info("Inside createUserSession(UserSession session) At {} ",new Date());
        log.info("Creating a user session record for {} ",session.getIssuedTo());
        userSessionsRepository.save(session);
    }

    @Override
    public void updateUserSession(UserSession session) {
        log.info("Inside updateUserSession(UserSession session) At {} ",new Date());
        log.info("Updating user session details");
        userSessionsRepository.save(session);
    }

    @Override
    public Optional<UserSession> findByIssuedTo(String issuedTo) {
        log.info("Inside findByIssuedTo(String issuedTo) At {} ",new Date());
        log.info("Fetching user session details by username");
        return userSessionsRepository.findByIssuedTo(issuedTo);
    }

    @Override
    public void logout(String username) {
        log.info("Inside logout(String username)");
        var sessionExists = userSessionsRepository.findByIssuedTo(username);
        if(sessionExists.isPresent())
        {
         var session = sessionExists.get();
         session.setLoggedIn(false);
         session.setAccessToken("");
         userSessionsRepository.save(session);
        }
    }
}
