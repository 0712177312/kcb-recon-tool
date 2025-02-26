package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.configurations.models.Menu;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface MenusService {
    List<Menu> findMenusAndSubMenusByListOfRoles(List<String> roles);
}