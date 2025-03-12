package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.configurations.entities.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CountriesRepository extends JpaRepository<Country,Long> {


    @Query(nativeQuery = true, value = "SELECT * FROM countries WHERE TRIM(name) = TRIM(:name)")
    Optional<Country> findByName(@Param("name") String name);

    @Query(nativeQuery = true,value = "SELECT * FROM countries WHERE status='Active'")
    List<Country> allWithoutPagination();

    @Query(nativeQuery = true,value = "SELECT * FROM countries WHERE status=:status")
    Page<Country> allByStatusWithPagination(@Param("status") String status, Pageable pageable);

    @Query(nativeQuery = true,value = "SELECT * FROM countries")
    Page<Country> allWithPagination(Pageable pageable);

}
