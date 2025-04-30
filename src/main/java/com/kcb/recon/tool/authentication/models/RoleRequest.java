package com.kcb.recon.tool.authentication.models;

import com.kcb.recon.tool.common.models.SubMenuDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
    private Long id;
    private String name;
    private String userName;
    private Long organization;
    private boolean status;
    List<Long> permissions = new ArrayList<>();
    private List<String> menus = new ArrayList<>();
    private List<String> submenus = new ArrayList<>();
}