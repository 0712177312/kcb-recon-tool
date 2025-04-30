package com.kcb.recon.tool.configurations.services;

import com.kcb.recon.tool.common.models.*;
import org.springframework.stereotype.Component;

@Component
public interface ConfigurationService {
    ConfigServiceResponse sendToConfigService(String request);

}