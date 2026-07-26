package org.lvmp.statementanalysis_springboot.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.exception.DatabaseException;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.DatabaseResumingException;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class RdsStatementExecutor {

    private static final int MAX_ATTEMPTS = 6;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(5);

    private final RdsDataClient client;

    public ExecuteStatementResponse execute(ExecuteStatementRequest request) {
        int attempt = 1;

        while (true) {
            try {
                return client.executeStatement(request);
            } catch (DatabaseResumingException e) {
                if (attempt >= MAX_ATTEMPTS) {
                    throw e;
                }

                log.warn("Aurora is resuming from auto-pause, retrying in {}s (attempt {}/{})",
                        RETRY_DELAY.getSeconds(), attempt, MAX_ATTEMPTS);
                sleep();
                attempt++;
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(RETRY_DELAY.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Interrupted while waiting for the database to resume", e);
        }
    }
}