package org.lvmp.statementanalysis_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.exception.AuthenticationException;
import org.lvmp.statementanalysis_springboot.model.ForcePasswordChangeRequest;
import org.lvmp.statementanalysis_springboot.model.LoginRequest;
import org.lvmp.statementanalysis_springboot.model.LoginResponse;
import org.lvmp.statementanalysis_springboot.model.LogoutRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CognitoAuthService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.client-secret}")
    private String clientSecret;
    @Value("${aws.cognito.client-id}")
    private String clientId;

    public ResponseEntity<LoginResponse> loginWithPassword(LoginRequest request) {
        var authParams = buildAuthParams(request.getUsername(), request.getPassword());

        try {
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(authParams)
                    .build();

            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);

            if (authResponse.challengeName() == ChallengeNameType.NEW_PASSWORD_REQUIRED) {
                return ResponseEntity.ok().body(
                        LoginResponse.builder()
                                .requiresPasswordChange(true)
                                .session(authResponse.session())
                                .build()
                );
            }

            AuthenticationResultType result = authResponse.authenticationResult();

            return ResponseEntity.ok().body(
                    LoginResponse.builder()
                            .gatewayToken(result.idToken())
                            .accessToken(result.accessToken())
                            .refreshToken(result.refreshToken())
                            .expiresIn(result.expiresIn())
                            .tokenType(result.tokenType())
                            .build()
            );

        } catch (NotAuthorizedException e) {
            log.warn("Failed login attempt for user: {}", request.getUsername());
            throw new AuthenticationException("Invalid username or password");

        } catch (UserNotFoundException e) {
            log.warn("Login attempt for non-existent user: {}", request.getUsername());
            throw new AuthenticationException("Invalid username or password");

        } catch (UserNotConfirmedException e) {
            throw new AuthenticationException("Account is not confirmed. Please check your email for a verification link.");
        } catch (TooManyRequestsException | LimitExceededException e) {
            log.warn("Rate limit hit for user: {}", request.getUsername());
            throw new AuthenticationException("Too many requests. Please try again later.");
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during Cognito authentication", e);
            throw new AuthenticationException("Authentication failed. Please try again.");
        }
    }

    public ResponseEntity<LoginResponse> forcePasswordChange(ForcePasswordChangeRequest request) {
        try {
            Map<String, String> challengeResponses = new HashMap<>();
            challengeResponses.put("USERNAME", request.getUsername());
            challengeResponses.put("NEW_PASSWORD", request.getNewPassword());
            challengeResponses.put("SECRET_HASH", computeSecretHash(request.getUsername()));

            RespondToAuthChallengeRequest challengeRequest = RespondToAuthChallengeRequest.builder()
                    .challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                    .clientId(clientId)
                    .session(request.getSession())
                    .challengeResponses(challengeResponses)
                    .build();

            RespondToAuthChallengeResponse challengeResponse = cognitoClient.respondToAuthChallenge(challengeRequest);
            AuthenticationResultType result = challengeResponse.authenticationResult();

            return ResponseEntity.ok().body(
                    LoginResponse.builder()
                            .gatewayToken(result.idToken())
                            .accessToken(result.accessToken())
                            .refreshToken(result.refreshToken())
                            .expiresIn(result.expiresIn())
                            .tokenType(result.tokenType())
                            .build()
            );

        } catch (Exception e) {
            log.error("Unexpected error during force password change", e);
            throw new AuthenticationException("Password change failed. Please try again.");
        }
    }

    public void logout(LogoutRequest request) {
        try {
            GlobalSignOutRequest signOutRequest = GlobalSignOutRequest.builder()
                    .accessToken(request.getAccessToken())
                    .build();
            cognitoClient.globalSignOut(signOutRequest);
        } catch (Exception e) {
            log.error("Unexpected error during logout", e);
            throw new AuthenticationException("Logout failed. Please try again.");
        }
    }

    private Map<String, String> buildAuthParams(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("USERNAME", username);
        params.put("PASSWORD", password);
        params.put("SECRET_HASH", computeSecretHash(username));

        return params;
    }

    private String computeSecretHash(String username) {
        try {
            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute Cognito SECRET_HASH", e);
        }
    }
}
