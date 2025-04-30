package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.configurations.extras.Menu1;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface MenusService {
    List<Menu1> findMenusAndSubMenusByListOfRoles(List<String> roles);
    List<Menu1> findAll();
}
