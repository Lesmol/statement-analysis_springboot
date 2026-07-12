package org.lvmp.statementanalysis_springboot.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserContext userContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            try {
                populateUserContext(authHeader.substring(BEARER_PREFIX.length()));
            } catch (Exception e) {
                log.warn("Failed to decode bearer token claims: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private void populateUserContext(String token) throws IOException {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            log.warn("Bearer token is not a well-formed JWT");
            return;
        }

        byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
        Map<String, Object> claims = objectMapper.readValue(payload, Map.class);

        userContext.setClaims(claims);
        userContext.setSub((String) claims.get("sub"));
        userContext.setUsername((String) claims.get("cognito:username"));
        userContext.setEmail((String) claims.get("email"));
        userContext.setEmailVerified(Boolean.TRUE.equals(claims.get("email_verified")));
        userContext.setPhoneNumber((String) claims.get("phone_number"));
        userContext.setPhoneNumberVerified(Boolean.TRUE.equals(claims.get("phone_number_verified")));
    }
}
