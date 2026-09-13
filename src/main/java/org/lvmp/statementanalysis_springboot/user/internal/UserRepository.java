package org.lvmp.statementanalysis_springboot.user.internal;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.shared.config.properties.ApplicationConfigurationProperties;
import org.lvmp.statementanalysis_springboot.shared.persistence.RdsStatementExecutor;
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
public class UserRepository {

    private final RdsStatementExecutor statementExecutor;
    private final ApplicationConfigurationProperties configurationProperties;

    public Optional<User> findById(UUID id) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT id, email, phone_number FROM users WHERE id = :id")
                .parameters(uuidParam("id", id))
                .build();

        ExecuteStatementResponse response = statementExecutor.execute(request);

        return response.records().stream()
                .findFirst()
                .map(this::mapRowToUser);
    }

    public Optional<User> findByEmail(String email) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT id, email, phone_number FROM users WHERE email = :email")
                .parameters(stringParam("email", email))
                .build();

        ExecuteStatementResponse response = statementExecutor.execute(request);

        return response.records().stream()
                .findFirst()
                .map(this::mapRowToUser);
    }

    public boolean existsByEmail(String email) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT 1 FROM users WHERE email = :email")
                .parameters(stringParam("email", email))
                .build();

        ExecuteStatementResponse response = statementExecutor.execute(request);

        return !response.records().isEmpty();
    }

    public void save(User user) {
        UUID id = user.getId() != null ? user.getId() : UUID.randomUUID();
        Instant now = Instant.now();

        ExecuteStatementRequest request = requestBuilder()
                .sql("INSERT INTO users (id, email, phone_number, created_at, updated_at) "
                        + "VALUES (:id, :email, :phoneNumber, :createdAt, :updatedAt)")
                .parameters(
                        uuidParam("id", id),
                        stringParam("email", user.getEmail()),
                        stringParam("phoneNumber", user.getPhoneNumber()),
                        timestampParam("createdAt", now),
                        timestampParam("updatedAt", now)
                )
                .build();

        statementExecutor.execute(request);

        user.toBuilder().id(id).createdAt(now).updatedAt(now).build();
    }

    public User update(User user) {
        Instant now = Instant.now();

        ExecuteStatementRequest request = requestBuilder()
                .sql("UPDATE users SET email = :email, phone_number = :phoneNumber, "
                        + "updated_at = :updatedAt WHERE id = :id")
                .parameters(
                        uuidParam("id", user.getId()),
                        stringParam("email", user.getEmail()),
                        stringParam("phoneNumber", user.getPhoneNumber()),
                        timestampParam("updatedAt", now)
                )
                .build();

        statementExecutor.execute(request);

        return user.toBuilder().updatedAt(now).build();
    }

    private ExecuteStatementRequest.Builder requestBuilder() {
        var rds = configurationProperties.rds();
        return ExecuteStatementRequest.builder()
                .resourceArn(rds.dbClusterArn())
                .secretArn(rds.dbSecretArn())
                .database(rds.dbName());
    }

    private User mapRowToUser(List<Field> row) {
        return User.builder()
                .id(UUID.fromString(row.get(0).stringValue()))
                .email(row.get(1).stringValue())
                .phoneNumber(row.get(2).stringValue())
                .build();
    }
}
