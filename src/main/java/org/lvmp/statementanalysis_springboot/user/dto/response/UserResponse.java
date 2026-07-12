package org.lvmp.statementanalysis_springboot.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String username;
    private String email;
    private boolean emailVerified;
    private String phoneNumber;
    private boolean phoneNumberVerified;
}
