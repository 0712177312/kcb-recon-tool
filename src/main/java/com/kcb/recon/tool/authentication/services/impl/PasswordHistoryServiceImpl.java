package com.kcb.recon.tool.authentication.services.impl;

import com.kcb.recon.tool.authentication.entities.PasswordHistory;
import com.kcb.recon.tool.authentication.entities.User;
import com.kcb.recon.tool.authentication.repositories.PasswordHistoryRepository;
import com.kcb.recon.tool.authentication.services.PasswordHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int passwordHistoryLimit = 3;

    @Override
    public boolean isRecentPassword(User user, String newPassword) {
        log.info("Inside isRecentPassword(User user, String newPassword) At {} ",new Date());
        log.info("Checking if user password was used recently in password history");
        // Fetch all password histories for this user
        List<PasswordHistory> passwordHistories = passwordHistoryRepository.findByUser(user);

        // Sort histories by createdAt, descending
        passwordHistories.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        // Check if the new password matches any of the recent passwords
        for (int i = 0; i < Math.min(passwordHistoryLimit, passwordHistories.size()); i++) {
            PasswordHistory history = passwordHistories.get(i);
            if (passwordEncoder.matches(newPassword, history.getPassword())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addPasswordHistory(User user, String encodedPassword) {
        log.info("Inside addPasswordHistory(User user, String encodedPassword) At {} ",new Date());
        log.info("Adding new record in password history table");
        // Fetch all password histories for this user
        List<PasswordHistory> passwordHistories = passwordHistoryRepository.findByUser(user);
        // Sort histories by entryDate, descending
        passwordHistories.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        // If history exceeds the limit, remove the oldest password
        if (passwordHistories.size() >= passwordHistoryLimit) {
            passwordHistoryRepository.delete(passwordHistories.get(passwordHistories.size() - 1));
        }
        // Create and save the new password history record
        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setPassword(encodedPassword);
        passwordHistory.setUser(user);
        passwordHistoryRepository.save(passwordHistory);
    }
}
