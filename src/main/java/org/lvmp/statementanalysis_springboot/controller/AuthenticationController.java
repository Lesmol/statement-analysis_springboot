package org.lvmp.statementanalysis_springboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.model.LoginRequest;
import org.lvmp.statementanalysis_springboot.model.LoginResponse;
import org.lvmp.statementanalysis_springboot.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login-with-password")
    public ResponseEntity<LoginResponse> loginWithPassword(@RequestBody @Valid LoginRequest request) {
        return authenticationService.loginWithPassword(request);
    }

}
