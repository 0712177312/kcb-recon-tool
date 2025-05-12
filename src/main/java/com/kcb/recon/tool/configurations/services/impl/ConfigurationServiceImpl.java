package com.kcb.recon.tool.configurations.services.impl;
import com.kcb.recon.tool.authentication.utils.OkHttpClientConfig;
import com.kcb.recon.tool.common.models.*;
import com.kcb.recon.tool.configurations.services.ConfigurationService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

    @Value("${config.microservice.api}")
    private String configServiceUrl;
    Gson gson = new Gson();
    private final OkHttpClientConfig okHttpClientConfig;

    public ConfigurationServiceImpl(OkHttpClientConfig okHttpClientConfig) {
        this.okHttpClientConfig = okHttpClientConfig;
    }

    @Override
    public ConfigServiceResponse sendToConfigService(String request,String token) {
        log.info("Config Request Request | {}", request);
        log.info("configServiceUrl | {}", configServiceUrl);
        ConfigServiceResponse res = new ConfigServiceResponse();
        RequestBody body = RequestBody.create(request, MediaType.get("application/json"));
        Request okhttpRequest = new Request.Builder()
                .url(configServiceUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();
        try {

          OkHttpClient client=okHttpClientConfig.createOkHttpClient();
            try (Response response = client.newCall(okhttpRequest).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : null;
                    res = gson.fromJson(responseBody, ConfigServiceResponse.class);
                    res.setStatus(true);
                    res.setCode(200);
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
            res.setCode(500);
            res.setMessage("Error | " + e.getLocalizedMessage());
        }
        return res;
    }

}