package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionsRepository extends JpaRepository<Permission,Long> {
    Optional<Permission> findByName(String name);
    @Query(nativeQuery = true,value = "SELECT * FROM permissions WHERE status='Active' AND validity_status='Approved'")
    Page<Permission> Paginated(Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM permissions WHERE status='Active' AND validity_status='Approved'")
    List<Permission> allPermissionsNoPagination();
}