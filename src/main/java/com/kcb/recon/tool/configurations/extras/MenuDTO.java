package com.kcb.recon.tool.configurations.extras;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MenuDTO {
    private String route;
    private String name;
    private String type;
    private String icon;
    private String badge;
    private MenuPermissions permissions;
    private List<MenuDTO> children = new ArrayList<>();

    @Data
    public static class MenuPermissions {
        private List<String> allowed;
        private List<String> denied;
    }
}
