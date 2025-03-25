package com.kcb.recon.tool.authentication.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kcb.recon.tool.authentication.models.AdResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LdapErrorHandler {

    private static final Gson gson = new Gson();

    private static final Map<String, String> ERROR_MAP = new HashMap<>();

    static {
        ERROR_MAP.put("52e", "Incorrect username or password.");
        ERROR_MAP.put("525", "User not found.");
        ERROR_MAP.put("530", "Not permitted to logon at this time.");
        ERROR_MAP.put("531", "Not permitted to logon from this workstation.");
        ERROR_MAP.put("532", "Password expired.");
        ERROR_MAP.put("533", "Account disabled.");
        ERROR_MAP.put("701", "Account expired.");
        ERROR_MAP.put("773", "User must reset password.");
        ERROR_MAP.put("775", "Account locked.");
    }

    public static String getFriendlyErrorMessage(AdResponse adResponseJson) {
        try {

            String jsonString = gson.toJson(adResponseJson);
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            if (jsonObject.has("message")) {
                String ldapMessage = jsonObject.get("message").getAsString();
                log.info("l-dap-message {}", ldapMessage);
                Pattern pattern = Pattern.compile("data\\s+([0-9a-fA-F]+)");

                log.info("pattern |{}", pattern);
                Matcher matcher = pattern.matcher(ldapMessage);
                log.info("matcher |{}", matcher);
                if (matcher.find()) {
                    String errorCode = matcher.group(1);
                    return ERROR_MAP.getOrDefault(errorCode, "Unknown LDAP authentication error.");
                }
            }
        } catch (Exception e) {
            String logMessage = AppUtillities.logPreString() + AppUtillities.ERROR + e.getMessage()
                    + AppUtillities.STACKTRACE + AppUtillities.getExceptionStacktrace(e);
            log.error("log-message -> {}", logMessage);
            return "Error processing LDAP response.";
        }
        return "No specific error code found in the response.";
    }

}
