package com.kcb.recon.tool.configurations.extras;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuPermission1 {
    @ElementCollection
    private List<String> allowed;

    @ElementCollection
    private List<String> denied;
}
