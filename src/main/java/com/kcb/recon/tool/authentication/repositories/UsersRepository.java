package com.kcb.recon.tool.authentication.repositories;

import com.kcb.recon.tool.authentication.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String emailAddress);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts  WHERE LOWER(username) = LOWER(:userName)")
    Optional<User> findByUsername(String userName);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE  status=:status and is_admin=1")
    Page<User> allSuperAdminAccountsWithPagination(@Param("status") String status, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts where is_admin=1")
    Page<User> allSuperAdminAccountsWithPagination(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts ua WHERE  ua.status='Active' AND ua.is_admin ='1'")
    List<User> allSuperAdminAccountsWithoutPagination();

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts  \n" +
            "WHERE subsidiary_id IS NOT NULL")
    Page<User> allAdminAccountsWithPagination(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts ua WHERE  ua.is_admin=1")
    List<User> allAdminAccountsWithoutPagination();

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts " +
            "WHERE  status=:status")
    Page<User> allAdminAccountsByStatusWithPagination(@Param("status") String status,
                                                      Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts where is_admin=0")
    Page<User> allUserAccountsWithPagination(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts ua where ua.is_admin='0'")
    List<User> allUserAccountsWithPagination();

    @Query(nativeQuery = true, value = "SELECT * FROM user_accounts" +
            " WHERE status=:status and is_admin=0")
    Page<User> allUserAccountsWithPaginationWithStatus(@Param("status") String status,
                                                       Pageable pageable);
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update user_accounts set logged_in=0 where id= ?1")
    void updateLoggedIn(Long id);

}