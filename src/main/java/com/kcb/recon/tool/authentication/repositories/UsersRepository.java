package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String emailAddress);



    @Query(nativeQuery = true,value = "SELECT * FROM user_accounts  WHERE LOWER(username) = LOWER(:userName)")
    Optional<User> findByUsername(String userName);

    /*--Filter Super Admin Accounts By Status With Pagination--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE  status=:status " +
            "AND account_type = :account_type")
    Page<User> allSuperAdminAccountsWithPagination(@Param("status") String status, @Param("account_type") Long account_type, Pageable pageable);

    /*--All Super Admin Accounts With Pagination--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts")
    Page<User> allSuperAdminAccountsWithPagination(@Param("account_type") Long account_type, Pageable pageable);

    /*--All Super Admin Accounts without Status Without Pagination--*/
    @Query(nativeQuery = true, value = "SELECT ua.* FROM user_accounts ua WHERE ua.country_id IS NULL AND ua.status='Active' AND ua.is_admin ='1'")
    List<User> allSuperAdminAccountsWithoutPagination();

    /*--All Country Admin Accounts without Status With Pagination--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts  \n" +
            "WHERE country_id IS NOT NULL AND branch_id IS NULL \n" +
            "AND account_type = :account_type")
    Page<User> allAdminAccountsWithPagination(@Param("account_type") Long account_type, Pageable pageable);

    /*--All Country Admin Accounts without Status Without Pagination--*/
    @Query(nativeQuery = true, value = "SELECT ua.* FROM user_accounts ua WHERE ua.country_id IS NOT NULL")
    List<User> allAdminAccountsWithoutPagination();

    /*--Filter Country Admin Accounts By Status,Organization & Account type With Pagination--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE organization_id IS NOT NULL AND organization_id=:orgId AND status=:status AND" +
            " branch_id IS NULL AND account_type IS NOT NULL AND account_type = :account_type")
    Page<User> allAdminAccountsByStatusWithPagination(@Param("status") String status,
                                                      @Param("orgId") Long orgId,
                                                      @Param("account_type") Long account_type,
                                                      Pageable pageable);

    /*--Filter Country Admin Accounts By Status & Account type With Pagination--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts WHERE organization_id IS NOT NULL" +
            " AND status=:status AND branch_id IS NULL AND account_type = :account_type")
    Page<User> allAdminAccountsByStatusWithPagination(@Param("status") String status,
                                                      @Param("account_type") Long account_type,
                                                      Pageable pageable);

    /*--Filter Admin Accounts By Organization & Account type With Pagination--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts" +
            " WHERE organization_id" +
            " IS NOT NULL AND organization_id=:orgId AND branch_id IS NULL" +
            " AND account_type = :account_type")
    Page<User> allAdminAccountsByOrganizationWithPagination(@Param("orgId") Long orgId,
                                                            @Param("account_type") Long account_type,
                                                            Pageable pageable);

    /*--All Admin Accounts By Organization Without Pagination--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts" +
            " WHERE organization_id IS NOT NULL AND" +
            " organization_id=:orgId AND branch_id IS NULL" +
            " AND account_type = :account_type")
    List<User> allAdminAccountsByOrganizationWithoutPagination(@Param("orgId") Long orgId
            , @Param("account_type") Long account_type);

    /*--Country User Accounts--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts")
    Page<User> allUserAccountsWithPaginationWithOrganizationOnly(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts ua where ua.is_admin='0'")
    List<User> allUserAccountsWithPagination( @Param("account_type") Long account_type);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts" +
            " WHERE status=:status")
    Page<User> allUserAccountsWithPaginationWithStatusAndOrganization(@Param("status") String status,
                                                                      Pageable pageable);

    /*--Review List--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE organization_id IS NOT NULL " +
            "AND branch_id IS NOT NULL AND organization_id=:orgId " +
            "AND validity_status=:validityStatus AND account_type != :account_type")
    Page<User> allUserAccountsWithPaginationWithStatusAndOrganizationForReviewList(@Param("validityStatus") String status,
                                                                                   @Param("account_type") Long account_type,
                                                                                   @Param("orgId") Long orgId, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE organization_id IS NOT NULL " +
            "AND branch_id IS NOT NULL AND organization_id=:orgId " +
            "AND validity_status='Pending' AND account_type != :account_type")
    Page<User> allUserAccountsWithPaginationWithOrganizationOnlyForReviewList(@Param("orgId") Long orgId,
                                                                              @Param("account_type") Long account_type,
                                                                              Pageable pageable);

    /*--Modifications Review List--*/
    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE organization_id IS NOT NULL " +
            "AND branch_id IS NOT NULL AND organization_id=:orgId " +
            "AND change_status='Pending' AND account_type != :account_type")
    Page<User> allUserAccountsWithPaginationWithOrganizationOnlyForModificationsReviewList(@Param("orgId") Long orgId,
                                                                                           @Param("account_type") Long account_type,
                                                                                           Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE organization_id IS NOT NULL " +
            "AND branch_id IS NOT NULL AND organization_id=:orgId " +
            "AND change_status=:changeStatus AND account_type != :account_type")
    Page<User> allUserAccountsWithPaginationWithStatusAndOrganizationForModificationsReviewList(@Param("changeStatus") String status,
                                                                                                @Param("orgId") Long orgId,
                                                                                                @Param("account_type") Long account_type,
                                                                                                Pageable pageable);






}