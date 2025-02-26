package com.kcb.recon.tool.common.services;

import com.kcb.recon.tool.authentication.models.PermissionRequest;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

@Component
public interface UtilitiesService {
    LocalDateTime addHours(LocalDateTime dateTime, long hoursToAdd);
    LocalDateTime addMinutes(LocalDateTime dateTime, long minutesToAdd);
    List<PermissionRequest> getAvailablePermissions();
    String generateOTPCode(int min, int max);
    String generatePassword(int length, String regexPolicy);
    String generateUserId();
    String generateUUID();
    String generateRandomChars(int length);
    String encryptAndSaveToFile(Object plainText,String password,String folder);
    String decryptFromFile(File file,String password);
    void writeToFile(File file, String content);
    Object readFromFile(File file);
    String generateSerialNo(int length);
    Connection getDatabaseConnection();
}