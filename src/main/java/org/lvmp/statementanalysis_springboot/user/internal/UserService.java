package org.lvmp.statementanalysis_springboot.user.internal;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.shared.context.UserContext;
import org.lvmp.statementanalysis_springboot.user.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserContext userContext;

    public ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(UserResponse.builder()
                .username(userContext.getUsername())
                .email(userContext.getEmail())
                .emailVerified(userContext.isEmailVerified())
                .phoneNumber(userContext.getPhoneNumber())
                .phoneNumberVerified(userContext.isPhoneNumberVerified())
                .build());
    }

}
