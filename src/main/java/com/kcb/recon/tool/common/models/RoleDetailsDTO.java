package com.kcb.recon.tool.common.models;


import com.kcb.recon.tool.configurations.extras.Menu1;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDetailsDTO {
    private Long id;
    private String name;
    private List<Menu1> menus;
//    private List<MenuWithSubmenusDTO> menus;
    private Date createdOn;
    private String createdBy;
    private String status;
}
