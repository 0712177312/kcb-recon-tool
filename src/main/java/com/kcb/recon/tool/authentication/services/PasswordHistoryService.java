package com.kcb.recon.tool.authentication.services;

import com.kcb.recon.tool.authentication.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface PasswordHistoryService {
    boolean isRecentPassword(User user,String password);
    void addPasswordHistory(User user, String encodedPassword);
}
