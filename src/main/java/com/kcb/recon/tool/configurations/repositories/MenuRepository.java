package com.kcb.recon.tool.configurations.repositories;

import com.kcb.recon.tool.configurations.extras.Menu1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu1, Integer> {
    List<Menu1> findAll();

}
