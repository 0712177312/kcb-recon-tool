package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.PasswordHistory;
import com.kcb.recon.tool.authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory,Long> {
    List<PasswordHistory> findByUser(User user);
}