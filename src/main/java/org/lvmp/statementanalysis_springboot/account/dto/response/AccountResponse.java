package org.lvmp.statementanalysis_springboot.account.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {
    private String accountId;
    private String accountName;
    private String maskedAccountNumber;
    private String accountType;
}
