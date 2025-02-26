package com.kcb.recon.tool.common.services.impl;
import com.kcb.recon.tool.authentication.models.ReconConfigRequest;
import com.kcb.recon.tool.common.models.*;
import com.kcb.recon.tool.common.services.ConfigurationService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

    @Value("${integration.adapter.base.url}")
    private String configServiceUrl;

    Gson gson = new Gson();
    @Value("${integration.adapter.base.url}")
    private String integrationAdapterBaseUrl;

    @Override
    public void sendNotification(NotificationsRequest request) {
        log.info("Inside sendNotification(NotificationsRequest request)");
        String jsonBody = gson.toJson(request);
        log.info("Notification Request | {}", jsonBody);

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
        Request okhttpRequest = new Request.Builder()
                .url(integrationAdapterBaseUrl + "Notifications/Send")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            OkHttpClient client = new OkHttpClient();
            try (Response response = client.newCall(okhttpRequest).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    log.info("Notification Request Successful: {}", responseBody);
                } else {
                    log.error("Notification API Responded with Status Code: {}", response.code());
                    log.error("Error Code: {}", response.code());
                }
            }
        } catch (IOException e) {
            log.error("Notification Request Failed: {}", e.getLocalizedMessage(), e);
        }
    }

    @Override
    public ConfigServiceResponse sendToConfigService(String request) {
        log.info("Config Request Request | {}", request);
        ConfigServiceResponse res = new ConfigServiceResponse();
        RequestBody body = RequestBody.create(request, MediaType.get("application/json"));
        Request okhttpRequest = new Request.Builder()
                .url(configServiceUrl + "/service/loan") //to be given appropriate url
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            try (Response response = client.newCall(okhttpRequest).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    res = gson.fromJson(responseBody, ConfigServiceResponse.class);
                    res.setStatus(true);
                    res.setData(responseBody);
                } else {
                    log.error("Config API Responded with Status Code: {}", response.code());
                    res.setStatus(false);
                    res.setMessage("Error Code: " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("Config Request Failed: {}", e.getLocalizedMessage());
            res.setStatus(false);
            res.setMessage("Error | " + e.getLocalizedMessage());
        }
        return res;
    }
    @Override
    public ConfigServiceResponse getConfigDetails() {
        log.info("Inside getConfigDetails() - Handling GET request");

        ConfigServiceResponse res = new ConfigServiceResponse();
        Request okhttpRequest = new Request.Builder()
                .url(configServiceUrl + "/get/use-case")
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            OkHttpClient client = new OkHttpClient();
            try (Response response = client.newCall(okhttpRequest).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    log.info("Config GET Request Successful: {}", responseBody);
                    res = gson.fromJson(responseBody, ConfigServiceResponse.class);
                } else {
                    log.error("Config API GET responded with Status Code: {}", response.code());
                    res.setStatus(false);
                    res.setMessage("Error Code: " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("Config GET Request Failed: {}", e.getLocalizedMessage());
            res.setStatus(false);
            res.setMessage("Error | " + e.getLocalizedMessage());
        }
        return res;
    }

}