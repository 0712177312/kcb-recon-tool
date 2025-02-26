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
    public LocalDateTime addHours(LocalDateTime dateTime, long hoursToAdd) {
        return dateTime.plusHours(hoursToAdd);
    }

    @Override
    public LocalDateTime addMinutes(LocalDateTime dateTime, long minutesToAdd) {
        return dateTime.plusMinutes(minutesToAdd);
    }

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
    public String generateOTPCode(int min, int max) {
        int randomNumber = random.nextInt(max - min + 1) + min;
        return String.format("%04d", randomNumber);
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

    @Override
    public String generateUserId() {
        int randomNumber = random.nextInt(99999 - 10000) + 10000;
        return "EQ"+randomNumber;
    }

    @Override
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateRandomChars(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomString = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(index));
        }
        return randomString.toString();
    }

    public char generateRandomCharacter() {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
        return charset.charAt(random.nextInt(charset.length()));
    }

    @Override
    public String encryptAndSaveToFile(Object plainText,String password,String folder) {
        try {
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPassword(password);
            String encryptedText = textEncryptor.encrypt(String.valueOf(plainText));
            String fileName = UUID.randomUUID() + ".txt";
            File file = new File(folder+fileName);
            writeToFile(file, encryptedText);
            return folder+fileName;
        } catch (Exception e) {
            log.error("Error during file encryption | {}", e.getMessage());
        }
        return null;
    }

    @Override
    public String decryptFromFile(File file,String password) {
        try {
            Object encryptedText = readFromFile(file);
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPassword(password);
            return textEncryptor.decrypt(encryptedText.toString());
        } catch (Exception e) {
            log.error("Error during file decryption | {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void writeToFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            log.error("Error writing to file | {} ", e.getMessage());
        }
    }

    @Override
    public Object readFromFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            log.error("Error reading to file | {} ", e.getMessage());
        }
        return content.toString();
    }

    @Override
    public String generateSerialNo(int length) {
        String digits = "0123456789";
        StringBuilder serialNo = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(digits.length());
            serialNo.append(digits.charAt(index));
        }

        return serialNo.toString();
    }

    @Override
    public Connection getDatabaseConnection(){
        try {
            Class.forName(dbClassname);
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        }catch (Exception e){
            log.error("Failed to create ORACLE DB Connection | {}",e.getMessage());
            return null;
        }
    }
}