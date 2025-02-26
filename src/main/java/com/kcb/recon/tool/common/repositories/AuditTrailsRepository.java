package com.kcb.recon.tool.common.repositories;

import com.kcb.recon.tool.common.entities.AuditTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailsRepository extends JpaRepository<AuditTrail,Long> {
    @Query(nativeQuery = true,value = "SELECT * FROM audit_trails where remote_user IS NOT NULL")
    Page<AuditTrail> allAuditTrails(Pageable pageable);
}
