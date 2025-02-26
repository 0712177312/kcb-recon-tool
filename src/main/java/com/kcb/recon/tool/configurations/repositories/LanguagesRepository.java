package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.configurations.entities.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LanguagesRepository extends JpaRepository<Language,Long> {
    Optional<Language> findByName(String name);
    @Query(nativeQuery = true, value = "SELECT * FROM languages WHERE status = :status")
    Page<Language> filterWithPaginationStatusProvided(@Param("status") String status, Pageable pageable);
    @Query(nativeQuery = true,value = "SELECT * FROM languages WHERE validity_status='Approved' AND status='Active'")
    List<Language> allWithoutPagination();
    @Query(nativeQuery = true,value = "SELECT * FROM languages")
    Page<Language> allWithPagination(Pageable pageable);
}
