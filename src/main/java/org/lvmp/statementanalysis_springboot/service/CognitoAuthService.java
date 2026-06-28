package org.lvmp.statementanalysis_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.exception.AuthenticationException;
import org.lvmp.statementanalysis_springboot.model.LoginRequest;
import org.lvmp.statementanalysis_springboot.model.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("aws.cognito.client-secret")
    private String clientSecret;
    @Value("aws.cognito.client-id")
    private String clientId;

    public LoginResponse login(LoginRequest request) {
        var authParams = buildAuthParams(request.getUsername(), request.getPassword());

        try {
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(authParams)
                    .build();

            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);

            if (authResponse.challengeName() == ChallengeNameType.NEW_PASSWORD_REQUIRED) {
                throw new AuthenticationException("Password reset required. Please update your temporary password before logging in.");
            }

            AuthenticationResultType result = authResponse.authenticationResult();

            return LoginResponse.builder()
                    .gatewayToken(result.idToken())
                    .accessToken(result.accessToken())
                    .refreshToken(result.refreshToken())
                    .expiresIn(result.expiresIn())
                    .tokenType(result.tokenType())
                    .build();

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

    private Map<String, String> buildAuthParams(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("USERNAME", username);
        params.put("PASSWORD", password);

        if (clientSecret != null && !clientSecret.isBlank()) {
            params.put("SECRET_HASH", computeSecretHash(username, clientSecret));
        }

        return params;
    }

    private String computeSecretHash(String username, String clientSecret) {
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
