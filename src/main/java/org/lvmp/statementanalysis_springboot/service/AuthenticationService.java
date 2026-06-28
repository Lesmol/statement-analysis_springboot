package org.lvmp.statementanalysis_springboot.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.model.LoginRequest;
import org.lvmp.statementanalysis_springboot.model.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final CognitoAuthService authService;

    public ResponseEntity<LoginResponse> loginWithPassword(@Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok().body(response);
    }

}
