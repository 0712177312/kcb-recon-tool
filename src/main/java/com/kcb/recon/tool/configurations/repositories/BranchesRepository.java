package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.configurations.entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchesRepository extends JpaRepository<Branch,Long> {
    Optional<Branch> findByName(String name);
    Optional<Branch> findByCode(String code);
    @Query(nativeQuery = true,value = "SELECT * FROM branches WHERE region_id = :regionId AND validity_status='Approved' AND status='Active'")
    List<Branch> allWithoutPaginationPerRegion(@Param("regionId") Long regionId);
    @Query(nativeQuery = true,value = "SELECT * FROM branches WHERE validity_status='Approved' AND status='Active'")
    List<Branch> allWithoutPagination();
    @Query(nativeQuery = true,value = "SELECT * FROM branches WHERE region_id = :regionId")
    List<Branch> allWithPaginationPerRegion(@Param("regionId") Long regionId);
    @Query(nativeQuery = true, value = "SELECT * FROM branches WHERE region_id = :regionId AND status = :status")
    List<Branch> filterWithPaginationStatusProvided(@Param("regionId") Long regionId,
                                                    @Param("status") String status);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId AND b.validity_status='Pending'")
    List<Branch> allWithPaginationPerOrganizationForReviewList(@Param("orgId") Long regionId);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId AND b.validity_status=:status")
    List<Branch> allWithPaginationPerOrganizationForReviewList(@Param("orgId") Long orgId,@Param("status") String status);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId AND b.change_status=:status")
    List<Branch> allWithPaginationPerOrgForModificationsReviewList(@Param("orgId") Long orgId,@Param("status") String status);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId AND b.change_status='Pending'")
    List<Branch> allWithPaginationPerOrgForModificationsReviewList(@Param("orgId") Long orgId);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId")
    List<Branch> allWithPaginationPerOrganization(@Param("orgId") Long orgId);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            " WHERE r.id = :regionId AND b.status=:status")
    List<Branch> allWithPaginationByRegionAndStatus(@Param("regionId") Long regionId,@Param("status") String status);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId AND b.status=:status")
    List<Branch> allWithPaginationPerOrganizationAndStatus(@Param("orgId") Long orgId,@Param("status") String status);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId AND b.status='Active'")
    List<Branch> allWithoutPaginationPerOrganization(@Param("orgId") Long orgId);
    @Query(nativeQuery = true,value = "SELECT b.* FROM branches b " +
            "INNER JOIN regions r ON b.region_id = r.id " +
            "INNER JOIN organizations o ON o.id=r.organization_id WHERE o.id = :orgId")
    List<Branch> allWithoutPaginationPerOrganizationNoFilters(@Param("orgId") Long orgId);
}
