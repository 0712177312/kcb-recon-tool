package com.kcb.recon.tool.common.services.impl;

import com.kcb.recon.tool.authentication.models.PermissionRequest;
import com.kcb.recon.tool.common.services.UtilitiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.*;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UtilitiesServiceImpl implements UtilitiesService {

    @Value("${private.key.path}")
    private String privateKeyPath;

    @Value("${public.key.path}")
    private String publicKeyPath;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbClassname;

    Random random = new Random();
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private static final SecureRandom secureRandom = new SecureRandom();


    @Override
    public List<PermissionRequest> getAvailablePermissions() {
        List<PermissionRequest> permissionRequests = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> mappings = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mappings.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            Set<String> patterns = mappingInfo.getDirectPaths();
            patterns.stream()
                    .filter(pattern -> pattern.startsWith("/api/v1") && !pattern.contains("Utils"))
                    .map(pattern -> pattern.substring("/api/v1/".length()))
                    .map(pattern -> pattern.replace("/", "_").toUpperCase())
                    .forEach(pattern -> {
                        permissionRequests.add(new PermissionRequest(null,pattern, pattern.replace("_", " "),"System"));
                    });
        }
        return permissionRequests;
    }

    @Override
    public String generatePassword(int length, String regexPolicy) {
        StringBuilder password = new StringBuilder();
        Pattern pattern = Pattern.compile(regexPolicy);
        while (true) {
            password.setLength(0);
            for (int i = 0; i < length; i++) {
                char randomChar = generateRandomCharacter();
                password.append(randomChar);
            }
            Matcher matcher = pattern.matcher(password);
            if (matcher.matches()) {
                return password.toString();
            }
        }
    }

    public char generateRandomCharacter() {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        return charset.charAt(random.nextInt(charset.length()));
    }

}