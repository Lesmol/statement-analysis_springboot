package org.lvmp.statementanalysis_springboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.model.ForcePasswordChangeRequest;
import org.lvmp.statementanalysis_springboot.model.LoginRequest;
import org.lvmp.statementanalysis_springboot.model.LoginResponse;
import org.lvmp.statementanalysis_springboot.model.LogoutRequest;
import org.lvmp.statementanalysis_springboot.service.CognitoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final CognitoAuthService authService;

    @PostMapping("/login-with-password")
    public ResponseEntity<LoginResponse> loginWithPassword(@RequestBody @Valid LoginRequest request) {
        return authService.loginWithPassword(request);
    }

    @PostMapping("/force-password-change")
    public ResponseEntity<LoginResponse> forcePasswordChange(@RequestBody @Valid ForcePasswordChangeRequest request) {
        return authService.forcePasswordChange(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

}
