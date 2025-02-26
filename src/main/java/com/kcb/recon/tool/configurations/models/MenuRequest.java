package com.kcb.recon.tool.configurations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRequest {
    private String route;
    private String name;
    private String type;
    private String icon;
    private MenuBadge badge;
    private List<String> onlyRoleNames;
    private List<String> exceptRoleName;
    private List<Long> subMenus;
}
