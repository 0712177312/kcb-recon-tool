package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Role,Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM roles WHERE TRIM(name) = TRIM(:name)")
    Optional<Role> findByName(@Param("name") String name);
    @Query(nativeQuery = true, value = "SELECT * FROM roles WHERE validity_status = :status")
    Page<Role> filterWithPaginationStatusProvidedForReviewList(@Param("status") String status, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE validity_status='Pending' AND status='Inactive'")
    Page<Role> filterWithPaginationForReviewListPendingOnly( Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles")
    List<Role> allWithoutPagination();
    @Query(nativeQuery = true, value = "SELECT * FROM roles WHERE status = :status")
    Page<Role> filterWithPaginationStatusProvided(@Param("status") String status, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE validity_status='Approved' AND status='Active'")
    List<Role> allWithoutPaginationForSuperAdmin();
    @Query(nativeQuery = true,value = "SELECT * FROM roles ")
    Page<Role> allAdminRolesWithPagination(Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE status =:status")
    Page<Role> filterAdminRolesWithPaginationStatusProvided(@Param("status") String status,Pageable pageable);

}