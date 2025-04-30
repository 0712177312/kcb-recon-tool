package com.kcb.recon.tool.common.services.impl;

import com.kcb.recon.tool.authentication.utils.AppUtillities;
import com.kcb.recon.tool.common.services.EncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {

    @Value("${private.key.path}")
    private String privateKeyPath;

    @Value("${public.key.path}")
    private String publicKeyPath;

    private final IvParameterSpec ivSpec = new IvParameterSpec(new byte[16]);

    @Override
    public String encrypt(String data, String encryptedKey) {
        try {
            log.info("Encrypting data using hybrid encryption (AES + RSA)");
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, loadPrivateKey());
            byte[] decryptedAesKeyBytes = rsaCipher.doFinal(Base64.decodeBase64(encryptedKey));
            SecretKey aesKey = new SecretKeySpec(decryptedAesKeyBytes, "AES");
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            byte[] encryptedData = aesCipher.doFinal(data.getBytes());
            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage()
                    + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
            log.error("log-message -> {}", logMessage);
            return null;
        }
    }

    @Override
    public String encryptAESKeyWithRSA() {
        log.info("Inside encryptAESKeyWithRSA(String rawKey) ");
        log.info("Manually encrypting AES key using RSA public key");
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, loadPublicKey());
            SecretKey aesKey = generateAESKey();
            assert aesKey != null;
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
            return Base64.encodeBase64String(encryptedAesKey);
        } catch (Exception e) {
            log.error("Error encrypting AES key | {} ", e.getMessage());
            return null;
        }
    }


    @Override
    public <T> T decrypt(String data, Class<T> type, String key) {
        try {
            log.info("Inside decrypt(String data, Class<T> type,String key) At {} ", new Date());
            log.info("Decrypting data using hybrid encryption (RSA + AES)");
            byte[] encryptedAesKey = Base64.decodeBase64(key);
            if (data.contains(":")) {
                data = data.split(":")[1];
            }
            byte[] encryptedData = Base64.decodeBase64(data);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, loadPrivateKey());
            byte[] decryptedAesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
            SecretKey aesKey = new SecretKeySpec(decryptedAesKeyBytes, "AES");
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            byte[] decryptedData = aesCipher.doFinal(encryptedData);
            String input = new String(decryptedData, StandardCharsets.UTF_8);
            String json = input
                    .replace("\\r\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1);
            }
            return new Gson().fromJson(json, type);
        } catch (Exception e) {
            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage()
                    + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
            log.error("log-message -> {}", logMessage);
            return null;
        }
    }

    @Override
    public String decrypt(String data, String key) {
        try {
            byte[] encryptedAesKey = Base64.decodeBase64(key);
            if (data.contains(":")) {
                data = data.split(":")[1];
            }
            byte[] encryptedData = Base64.decodeBase64(data);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, loadPrivateKey());
            byte[] decryptedAesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
            SecretKey aesKey = new SecretKeySpec(decryptedAesKeyBytes, "AES");
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
            byte[] decryptedData = aesCipher.doFinal(encryptedData);
            String input = new String(decryptedData, StandardCharsets.UTF_8);
            String json = input
                    .replace("\\r\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1);
            }
            return json;
        } catch (Exception e) {
            log.error("Error decrypting data using RSA and AES {} ", e.getMessage());
            return null;
        }
    }

    private SecretKey generateAESKey() {
        log.info("Inside generateAESKey()");
        log.info("Generating AES Key");
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to Generate AES Key -> {} ", e.getMessage());
            return null;
        }
    }

    public PublicKey loadPublicKey() {
        try {
            log.info("Loading public key from path: {}", publicKeyPath);
            String publicKeyPem = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
            return getPublicKeyFromPem(publicKeyPem);
        } catch (Exception e) {
            log.error("Failed to load public key", e);
            return null;
        }
    }

    public PrivateKey loadPrivateKey() {
        try {
            String privateKeyPem = new String(Files.readAllBytes(Paths.get(privateKeyPath)));
            return getPrivateKeyFromPem(privateKeyPem);
        } catch (Exception e) {
            log.error("Failed to load private key", e);
            return null;
        }
    }

    public static PrivateKey getPrivateKeyFromPem(String pem) throws Exception {
        String privateKeyPEM = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        privateKeyPEM = privateKeyPEM.replace('-', '+').replace('_', '/');
        byte[] encoded = Base64.decodeBase64(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    public static PublicKey getPublicKeyFromPem(String pem) throws Exception {
        String publicKeyPEM = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.decodeBase64(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    }
}