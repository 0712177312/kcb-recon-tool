package com.kcb.recon.tool.configurations.extras;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "menu")
public class SubMenu1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String route;
    private String name;
    private String type;
    private String icon;
    private String badge;
//    @Column(name="menu_id")
//    private int menuId;
    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    @JsonBackReference
    private Menu1 menu;

    @Embedded
    private SubMenuPermission1 permissions;
}
