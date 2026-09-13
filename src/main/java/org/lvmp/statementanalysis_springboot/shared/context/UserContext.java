package org.lvmp.statementanalysis_springboot.shared.context;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Map;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Data
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = TARGET_CLASS)
public class UserContext {
    private String sub;
    private String username;
    private String email;
    private boolean emailVerified;
    private String phoneNumber;
    private boolean phoneNumberVerified;
    private Map<String, Object> claims = Collections.emptyMap();
}
