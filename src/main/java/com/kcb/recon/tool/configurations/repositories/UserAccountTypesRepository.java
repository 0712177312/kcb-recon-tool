package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.configurations.entities.UserAccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountTypesRepository extends JpaRepository<UserAccountType,Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM user_account_types WHERE TRIM(name) = TRIM(:name)")
    Optional<UserAccountType> findByName(@Param("name") String name);
    @Query(nativeQuery = true, value = "SELECT * FROM user_account_types WHERE status = :status")
    Page<UserAccountType> filterWithPaginationStatusProvided(@Param("status") String status, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM user_account_types WHERE validity_status='Approved' AND status='Active'")
    List<UserAccountType> allWithoutPagination();
    @Query(nativeQuery = true,value = "SELECT * FROM user_account_types")
    Page<UserAccountType> allWithPagination(Pageable pageable);
}
