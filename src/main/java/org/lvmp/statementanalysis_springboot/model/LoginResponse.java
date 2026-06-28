package org.lvmp.statementanalysis_springboot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String gatewayToken;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String tokenType;
    private boolean requiresPasswordChange;
    private String session;
}
