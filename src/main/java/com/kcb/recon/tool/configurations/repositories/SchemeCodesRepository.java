package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.configurations.entities.SchemeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SchemeCodesRepository extends JpaRepository<SchemeCode,Long> {
    Optional<SchemeCode> findByName(String name);
}
