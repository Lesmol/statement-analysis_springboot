package org.lvmp.statementanalysis_springboot.user.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.authentication.UserAuthenticated;
import org.lvmp.statementanalysis_springboot.shared.exception.DatabaseException;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserProvisioningListener {
    private final UserRepository userRepository;

    @ApplicationModuleListener
    void on(UserAuthenticated event) {
        if (userRepository.existsByEmail(event.email())) {
            return;
        }

        try {
            User user = User.builder()
                    .id(UUID.fromString(event.sub()))
                    .email(event.email())
                    .phoneNumber(event.phoneNumber())
                    .build();

            userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to provision user {} in the database", event.email(), e);
            throw new DatabaseException("Failed to save user to the database", e);
        }
    }
}
