package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.configurations.entities.Subsidiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitializeRepository  extends JpaRepository<Subsidiary,Long> {
}
