package org.lvmp.statementanalysis_springboot.account.internal;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.account.AccountType;
import org.lvmp.statementanalysis_springboot.shared.config.properties.ApplicationConfigurationProperties;
import org.lvmp.statementanalysis_springboot.shared.persistence.RdsStatementExecutor;
import org.lvmp.statementanalysis_springboot.shared.persistence.SqlParameters;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.lvmp.statementanalysis_springboot.shared.persistence.SqlParameters.stringParam;
import static org.lvmp.statementanalysis_springboot.shared.persistence.SqlParameters.timestampParam;
import static org.lvmp.statementanalysis_springboot.shared.persistence.SqlParameters.uuidParam;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final RdsStatementExecutor statementExecutor;
    private final ApplicationConfigurationProperties configurationProperties;

    public Optional<Account> findById(UUID id) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT id, user_id, account_name, account_number, account_type, created_at, updated_at "
                        + "FROM accounts WHERE id = :id")
                .parameters(uuidParam("id", id))
                .build();

        ExecuteStatementResponse response = statementExecutor.execute(request);

        return response.records().stream()
                .findFirst()
                .map(this::mapRowToAccount);
    }

    public List<Account> findByUserId(UUID userId) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT id, user_id, account_name, account_number, account_type, created_at, updated_at "
                        + "FROM accounts WHERE user_id = :userId")
                .parameters(uuidParam("userId", userId))
                .build();

        ExecuteStatementResponse response = statementExecutor.execute(request);

        return response.records().stream()
                .map(this::mapRowToAccount)
                .toList();
    }

    public void save(Account account) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        ExecuteStatementRequest request = requestBuilder()
                .sql("INSERT INTO accounts (id, user_id, account_name, account_number, account_type, created_at, updated_at) "
                        + "VALUES (:id, :userId, :accountName, :accountNumber, :accountType, :createdAt, :updatedAt)")
                .parameters(
                        uuidParam("id", id),
                        uuidParam("userId", account.getUserId()),
                        stringParam("accountName", account.getAccountName()),
                        stringParam("accountNumber", account.getAccountNumber()),
                        stringParam("accountType", account.getAccountType().name()),
                        timestampParam("createdAt", now),
                        timestampParam("updatedAt", now)
                )
                .build();

        statementExecutor.execute(request);

        account.toBuilder().id(id).createdAt(now).updatedAt(now).build();
    }

    public void update(Account account) {
        Instant now = Instant.now();

        ExecuteStatementRequest request = requestBuilder()
                .sql("UPDATE accounts SET account_name = :accountName, account_number = :accountNumber, "
                        + "account_type = :accountType, updated_at = :updatedAt WHERE id = :id")
                .parameters(
                        uuidParam("id", account.getId()),
                        stringParam("accountName", account.getAccountName()),
                        stringParam("accountNumber", account.getAccountNumber()),
                        stringParam("accountType", account.getAccountType().name()),
                        timestampParam("updatedAt", now)
                )
                .build();

        statementExecutor.execute(request);

        account.toBuilder().updatedAt(now).build();
    }

    private ExecuteStatementRequest.Builder requestBuilder() {
        var rds = configurationProperties.rds();
        return ExecuteStatementRequest.builder()
                .resourceArn(rds.dbClusterArn())
                .secretArn(rds.dbSecretArn())
                .database(rds.dbName());
    }

    private Account mapRowToAccount(List<Field> row) {
        return Account.builder()
                .id(UUID.fromString(row.get(0).stringValue()))
                .userId(UUID.fromString(row.get(1).stringValue()))
                .accountName(row.get(2).stringValue())
                .accountNumber(row.get(3).stringValue())
                .accountType(AccountType.valueOf(row.get(4).stringValue()))
                .createdAt(SqlParameters.parseTimestamp(row.get(5).stringValue()))
                .updatedAt(SqlParameters.parseTimestamp(row.get(6).stringValue()))
                .build();
    }
}