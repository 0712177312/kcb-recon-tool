package com.kcb.recon.tool.configurations.extras;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@ToString(exclude = "children")
public class Menu1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String route;
    private String name;
    private String type;
    private String icon;
    private String badge;
    private Long menuId;
    @Embedded
    private MenuPermission1 permissions;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SubMenu1> children;


}
