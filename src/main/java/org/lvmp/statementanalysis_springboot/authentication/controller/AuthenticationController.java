package org.lvmp.statementanalysis_springboot.authentication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.authentication.dto.request.ForcePasswordChangeRequest;
import org.lvmp.statementanalysis_springboot.authentication.dto.request.LoginRequest;
import org.lvmp.statementanalysis_springboot.authentication.dto.request.SignupRequest;
import org.lvmp.statementanalysis_springboot.authentication.dto.response.LoginResponse;
import org.lvmp.statementanalysis_springboot.authentication.dto.request.LogoutRequest;
import org.lvmp.statementanalysis_springboot.authentication.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/login-with-password")
    public ResponseEntity<LoginResponse> loginWithPassword(@RequestBody @Valid LoginRequest request) {
        return authService.loginWithPassword(request);
    }

    @PostMapping("/force-password-change")
    public ResponseEntity<LoginResponse> forcePasswordChange(@RequestBody @Valid ForcePasswordChangeRequest request) {
        return authService.forcePasswordChange(request);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignupRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

}
