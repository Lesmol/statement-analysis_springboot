package org.lvmp.statementanalysis_springboot.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lvmp.statementanalysis_springboot.enums.AccountType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @NotBlank
    private String accountName;
    @NotBlank
    private String accountNumber;
    @NotBlank
    private AccountType accountType;
}
