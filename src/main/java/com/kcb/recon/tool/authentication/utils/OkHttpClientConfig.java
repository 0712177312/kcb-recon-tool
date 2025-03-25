package com.kcb.recon.tool.authentication.utils;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpClientConfig {


    @Value("${http-connection-timeout}")
    private int connectionConnectTimeout;

    @Value("${http-connection-read-timeout}")
    private int connectionReadTimeout;

    @Value("${http-connection-write-timeout}")
    private int connectionWriteTimeout;

    @Bean
    public OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(connectionConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(connectionReadTimeout, TimeUnit.SECONDS)
                .writeTimeout(connectionWriteTimeout, TimeUnit.SECONDS)
                .build();
    }
}
