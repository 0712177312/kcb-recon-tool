package com.kcb.recon.tool.common.services;

import org.springframework.stereotype.Component;

@Component
public interface EncryptionService {
    String encryptAESKeyWithRSA();
    <T> T decrypt(String data,Class<T> type,String key);
    String encrypt(String data,String key);
}