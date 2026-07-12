package org.lvmp.statementanalysis_springboot.context;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Map;

@Data
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class UserContext {

    private String sub;
    private String username;
    private String email;
    private boolean emailVerified;
    private String phoneNumber;
    private boolean phoneNumberVerified;
    private Map<String, Object> claims = Collections.emptyMap();

    public boolean isAuthenticated() {
        return sub != null;
    }
}
