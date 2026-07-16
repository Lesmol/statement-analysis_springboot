package org.lvmp.statementanalysis_springboot.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String gatewayToken;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String tokenType;
    private Boolean requiresPasswordChange;
    private String session;
}
