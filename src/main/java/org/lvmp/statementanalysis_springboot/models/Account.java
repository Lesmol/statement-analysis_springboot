package org.lvmp.statementanalysis_springboot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lvmp.statementanalysis_springboot.enums.AccountType;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Account {
    private UUID id;
    private UUID userId;
    private String accountName;
    private String accountNumber;
    private AccountType accountType;
    private Instant createdAt;
    private Instant updatedAt;
}