package com.kcb.recon.tool.authentication.utils;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) { }
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true) // Trust all hostnames
                    .connectTimeout(connectionConnectTimeout, TimeUnit.SECONDS)
                    .readTimeout(connectionReadTimeout, TimeUnit.SECONDS)
                    .writeTimeout(connectionWriteTimeout, TimeUnit.SECONDS)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create a trusting OkHttpClient", e);
        }
    }

}
