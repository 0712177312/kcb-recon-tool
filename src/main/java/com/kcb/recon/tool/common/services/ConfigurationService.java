package com.kcb.recon.tool.common.services;

import com.kcb.recon.tool.authentication.models.ReconConfigRequest;
import com.kcb.recon.tool.common.models.*;
import org.springframework.stereotype.Component;

@Component
public interface ConfigurationService {
    void sendNotification(NotificationsRequest request);

    ConfigServiceResponse sendToConfigService(String request);

    ConfigServiceResponse getConfigDetails();
}