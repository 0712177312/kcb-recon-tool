package com.kcb.recon.tool.authentication.utils;

import com.kcb.recon.tool.authentication.models.AdResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Properties;
import java.util.logging.Logger;

@Component
@Getter
@Setter
public class ActiveDirectory {
    private static final Logger logger = Logger.getLogger(ActiveDirectory.class.getName());

    @Value("${ldap.domain}")
    private String domain;

    @Value("${ldap.host}")
    private String host;

    private String username;
    private String password;

    public AdResponse login(String username, String password) {
        AdResponse adResponse = new AdResponse();
        this.username = createUsername(username);
        this.password = password;

        try {
            DirContext ctx = new InitialDirContext(createConnectionProperties());
            ctx.close();
            adResponse.setMessage("Successfully logged in");
            adResponse.setCode(200);
        } catch (NamingException e) {
            logger.warning("Failed to authenticate user: " + e.getMessage());
            adResponse.setCode(400);
            adResponse.setMessage( e.getMessage());
        }
        return adResponse;
    }
    private Properties createConnectionProperties() {
        Properties prop = new Properties();
        prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        prop.put(Context.PROVIDER_URL, this.createUrl());
        prop.put(Context.SECURITY_AUTHENTICATION, "simple");
        prop.put(Context.SECURITY_PRINCIPAL, this.getUsername());
        prop.put(Context.SECURITY_CREDENTIALS, this.getPassword());

        return prop;
    }

    private String createDC() {
        char[] namePair = this.getDomain().toUpperCase().toCharArray();
        StringBuilder dn = new StringBuilder("CN=Users,DC=");
        for (int i = 0; i < namePair.length; i++) {
            if (namePair[i] == '.') {
                dn.append(",DC=").append(namePair[++i]);

            } else {
                dn.append(namePair[i]);
            }
        }
        return dn.toString();
    }


    private String createUrl() {
        return "ldap://" + getHost() + "/" + createDC();
    }

    private String createUsername(String username) {
        if (!username.contains("@")) {
            username = username + "@" + this.getDomain();
        }
        return username;
    }
}
