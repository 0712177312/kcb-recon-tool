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


    private final UserSessionsRepository userSessionsRepository;

    public UserSessionsServiceImpl(UserSessionsRepository userSessionsRepository) {
        this.userSessionsRepository = userSessionsRepository;
    }

    @Override
    public void createUserSession(UserSession session) {
        log.info("Inside createUserSession(UserSession session) At {} ",new Date());
        log.info("Creating a user session record for {} ",session.getIssuedTo());
        userSessionsRepository.save(session);
    }

//    @Override
//    public void updateUserSession(UserSession session) {
//        UserSession userSession1=new UserSession();
//        userSession1.setUser(session.getUser());
//        log.info("Inside updateUserSession(UserSession session) At {} ",new Date());
//        log.info("Updating user session details");
//        userSessionsRepository.save(userSession1);
//    }

    @Override
    public void updateUserSession(UserSession session) {

        Optional<UserSession> existingSessionOpt = userSessionsRepository.findById(session.getId());

        if (existingSessionOpt.isPresent()) {
            UserSession existingSession = existingSessionOpt.get();
            existingSession.setUser(session.getUser());
            userSessionsRepository.save(existingSession);
        } else {
            userSessionsRepository.save(session); // Insert as new if not found
        }
    }


    @Override
    public Optional<UserSession> findByIssuedTo(String issuedTo) {
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
