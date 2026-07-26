package org.lvmp.statementanalysis_springboot.repository;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.models.Account;
import org.lvmp.statementanalysis_springboot.enums.AccountType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final RdsStatementExecutor statementExecutor;
    @Value("${aws.rds.db-cluster-arn}")
    private String CLUSTER_ARN;
    @Value("${aws.rds.db-secret-arn}")
    private String SECRET_ARN;
    @Value("${aws.rds.db-name}")
    private String DATABASE;

    public Optional<Account> findById(UUID id) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT id, user_id, account_name, account_number, account_type, created_at, updated_at "
                        + "FROM accounts WHERE id = :id")
                .parameters(SqlParameter.builder()
                        .name("id")
                        .value(Field.builder().stringValue(id.toString()).build())
                        .build())
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
                .parameters(SqlParameter.builder()
                        .name("userId")
                        .value(Field.builder().stringValue(userId.toString()).build())
                        .build())
                .build();

        ExecuteStatementResponse response = statementExecutor.execute(request);

        return response.records().stream()
                .map(this::mapRowToAccount)
                .toList();
    }

    public void save(Account account) {
        UUID id = account.getId() != null ? account.getId() : UUID.randomUUID();
        Instant now = Instant.now();

        ExecuteStatementRequest request = requestBuilder()
                .sql("INSERT INTO accounts (id, user_id, account_name, account_number, account_type, created_at, updated_at) "
                        + "VALUES (:id, :userId, :accountName, :accountNumber, :accountType, :createdAt, :updatedAt)")
                .parameters(
                        SqlParameter.builder().name("id").value(Field.builder().stringValue(id.toString()).build()).build(),
                        SqlParameter.builder().name("userId").value(Field.builder().stringValue(account.getUserId().toString()).build()).build(),
                        SqlParameter.builder().name("accountName").value(Field.builder().stringValue(account.getAccountName()).build()).build(),
                        SqlParameter.builder().name("accountNumber").value(Field.builder().stringValue(account.getAccountNumber()).build()).build(),
                        SqlParameter.builder().name("accountType").value(Field.builder().stringValue(account.getAccountType().name()).build()).build(),
                        SqlParameter.builder().name("createdAt").value(Field.builder().stringValue(now.toString()).build()).build(),
                        SqlParameter.builder().name("updatedAt").value(Field.builder().stringValue(now.toString()).build()).build()
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
                        SqlParameter.builder().name("id").value(Field.builder().stringValue(account.getId().toString()).build()).build(),
                        SqlParameter.builder().name("accountName").value(Field.builder().stringValue(account.getAccountName()).build()).build(),
                        SqlParameter.builder().name("accountNumber").value(Field.builder().stringValue(account.getAccountNumber()).build()).build(),
                        SqlParameter.builder().name("accountType").value(Field.builder().stringValue(account.getAccountType().name()).build()).build(),
                        SqlParameter.builder().name("updatedAt").value(Field.builder().stringValue(now.toString()).build()).build()
                )
                .build();

        statementExecutor.execute(request);

        account.toBuilder().updatedAt(now).build();
    }

    private ExecuteStatementRequest.Builder requestBuilder() {
        return ExecuteStatementRequest.builder()
                .resourceArn(CLUSTER_ARN)
                .secretArn(SECRET_ARN)
                .database(DATABASE);
    }

    private Account mapRowToAccount(List<Field> row) {
        return Account.builder()
                .id(UUID.fromString(row.get(0).stringValue()))
                .userId(UUID.fromString(row.get(1).stringValue()))
                .accountName(row.get(2).stringValue())
                .accountNumber(row.get(3).stringValue())
                .accountType(AccountType.valueOf(row.get(4).stringValue()))
                .createdAt(Instant.parse(row.get(5).stringValue()))
                .updatedAt(Instant.parse(row.get(6).stringValue()))
                .build();
    }
}