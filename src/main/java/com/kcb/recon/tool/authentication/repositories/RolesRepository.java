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
    Optional<Role> findByNameAndOrganization(String name,Long organization);
    @Query(nativeQuery = true, value = "SELECT * FROM roles WHERE validity_status = :status")
    Page<Role> filterWithPaginationStatusProvidedForReviewList(@Param("status") String status, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE validity_status='Pending' AND status='Inactive'")
    Page<Role> filterWithPaginationForReviewListPendingOnly( Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE organization=:organization")
    Page<Role> allWithPagination(@Param("organization") Long organization,Pageable pageable);
    @Query(nativeQuery = true, value = "SELECT * FROM roles WHERE status = :status AND organization=:organization")
    Page<Role> filterWithPaginationStatusProvided(@Param("organization") Long organization,@Param("status") String status, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE validity_status='Approved' AND status='Active' AND organization IS NULL")
    List<Role> allWithoutPaginationForSuperAdmin();
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE validity_status='Approved' AND status='Active' AND organization=:organization")
    List<Role> allWithoutPaginationForAdmin(@Param("organization") Long organization);
    @Query(nativeQuery = true,value = "SELECT * FROM roles")
    List<Role> allWithoutPaginationForOrganizationNoFilter();
    @Query(nativeQuery = true, value = "SELECT * FROM roles WHERE change_status = :status AND organization IS NOT NULL AND organization=:organization")
    Page<Role> filterWithPaginationStatusProvidedForModificationsReviewList(@Param("organization") Long organization,@Param("status") String status, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE change_status='Pending' AND organization IS NOT NULL AND organization=:organization")
    Page<Role> filterWithPaginationForModificationsReviewListPendingOnly(@Param("organization") Long organization, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE organization IS NULL")
    Page<Role> allAdminRolesWithPagination(Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM roles WHERE organization IS NULL AND status =:status")
    Page<Role> filterAdminRolesWithPaginationStatusProvided(@Param("status") String status,Pageable pageable);

}