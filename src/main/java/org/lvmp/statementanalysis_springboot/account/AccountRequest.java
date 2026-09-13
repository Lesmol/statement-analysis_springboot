package org.lvmp.statementanalysis_springboot.account;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @NotBlank
    private String accountName;
    @NotBlank
    private String accountNumber;
    @NonNull
    private AccountType accountType;
}
