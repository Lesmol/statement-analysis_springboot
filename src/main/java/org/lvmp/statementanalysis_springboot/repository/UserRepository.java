package org.lvmp.statementanalysis_springboot.repository;

import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
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
public class UserRepository {

    private final RdsDataClient client;
    @Value("${aws.rds.db-cluster-arn}")
    private String CLUSTER_ARN;
    @Value("${aws.rds.db-secret-arn}")
    private String SECRET_ARN;
    @Value("${aws.rds.db-name}")
    private String DATABASE;

    public Optional<User> findById(UUID id) {
        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .resourceArn(CLUSTER_ARN)
                .secretArn(SECRET_ARN)
                .database(DATABASE)
                .sql("SELECT id, email, phone_number FROM users WHERE id = :id")
                .parameters(SqlParameter.builder()
                        .name("id")
                        .value(Field.builder().stringValue(id.toString()).build())
                        .build())
                .build();

        ExecuteStatementResponse response = client.executeStatement(request);

        return response.records().stream()
                .findFirst()
                .map(this::mapRowToUser);
    }

    public Optional<User> findByEmail(String email) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT id, email, phone_number FROM users WHERE email = :email")
                .parameters(SqlParameter.builder()
                        .name("email")
                        .value(Field.builder().stringValue(email).build())
                        .build())
                .build();

        ExecuteStatementResponse response = client.executeStatement(request);

        return response.records().stream()
                .findFirst()
                .map(this::mapRowToUser);
    }

    public boolean existsByEmail(String email) {
        ExecuteStatementRequest request = requestBuilder()
                .sql("SELECT 1 FROM users WHERE email = :email")
                .parameters(SqlParameter.builder()
                        .name("email")
                        .value(Field.builder().stringValue(email).build())
                        .build())
                .build();

        ExecuteStatementResponse response = client.executeStatement(request);

        return !response.records().isEmpty();
    }

    public void save(User user) {
        UUID id = user.getId() != null ? user.getId() : UUID.randomUUID();
        Instant now = Instant.now();

        ExecuteStatementRequest request = requestBuilder()
                .sql("INSERT INTO users (id, email, phone_number, created_at, updated_at) "
                        + "VALUES (:id, :email, :phoneNumber, :createdAt, :updatedAt)")
                .parameters(
                        SqlParameter.builder().name("id").value(Field.builder().stringValue(id.toString()).build()).build(),
                        SqlParameter.builder().name("email").value(Field.builder().stringValue(user.getEmail()).build()).build(),
                        SqlParameter.builder().name("phoneNumber").value(Field.builder().stringValue(user.getPhoneNumber()).build()).build(),
                        SqlParameter.builder().name("createdAt").value(Field.builder().stringValue(now.toString()).build()).build(),
                        SqlParameter.builder().name("updatedAt").value(Field.builder().stringValue(now.toString()).build()).build()
                )
                .build();

        client.executeStatement(request);

        user.toBuilder().id(id).createdAt(now).updatedAt(now).build();
    }

    public User update(User user) {
        Instant now = Instant.now();

        ExecuteStatementRequest request = requestBuilder()
                .sql("UPDATE users SET email = :email, phone_number = :phoneNumber, "
                        + "updated_at = :updatedAt WHERE id = :id")
                .parameters(
                        SqlParameter.builder().name("id").value(Field.builder().stringValue(user.getId().toString()).build()).build(),
                        SqlParameter.builder().name("email").value(Field.builder().stringValue(user.getEmail()).build()).build(),
                        SqlParameter.builder().name("phoneNumber").value(Field.builder().stringValue(user.getPhoneNumber()).build()).build(),
                        SqlParameter.builder().name("updatedAt").value(Field.builder().stringValue(now.toString()).build()).build()
                )
                .build();

        client.executeStatement(request);

        return user.toBuilder().updatedAt(now).build();
    }

    private ExecuteStatementRequest.Builder requestBuilder() {
        return ExecuteStatementRequest.builder()
                .resourceArn(CLUSTER_ARN)
                .secretArn(SECRET_ARN)
                .database(DATABASE);
    }

    private User mapRowToUser(List<Field> row) {
        return User.builder()
                .id(UUID.fromString(row.get(0).stringValue()))
                .email(row.get(1).stringValue())
                .phoneNumber(row.get(2).stringValue())
                .build();
    }
}
