package org.lvmp.statementanalysis_springboot.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lvmp.statementanalysis_springboot.account.dto.response.AccountResponse;
import org.lvmp.statementanalysis_springboot.account.dto.request.AccountRequest;
import org.lvmp.statementanalysis_springboot.context.UserContext;
import org.lvmp.statementanalysis_springboot.models.Account;
import org.lvmp.statementanalysis_springboot.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private static final String MASK = "****";
    private final AccountRepository accountRepository;
    private final UserContext userContext;

    public ResponseEntity<AccountResponse> getAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        return ResponseEntity.ok().body(toResponse(account));
    }

    public ResponseEntity<Void> createAccount(AccountRequest request) {
        UUID userId = UUID.fromString(userContext.getSub());

        Account account = Account.builder()
                .userId(userId)
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .accountType(request.getAccountType())
                .build();

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<Void> updateAccount(UUID accountId, AccountRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        Account updatedAccount = account.toBuilder()
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .accountType(request.getAccountType())
                .build();

        accountRepository.update(updatedAccount);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<AccountResponse>> getAccounts() {
        UUID userId = UUID.fromString(userContext.getSub());
        List<Account> accounts = accountRepository.findByUserId(userId);

        return ResponseEntity.ok().body(mapAccountsResponse(accounts));
    }

    private List<AccountResponse> mapAccountsResponse(List<Account> accounts) {
        return accounts.stream()
                .map(this::toResponse)
                .toList();
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getId().toString())
                .accountName(account.getAccountName())
                .maskedAccountNumber(maskAccountNumber(account.getAccountNumber()))
                .accountType(account.getAccountType().name())
                .build();
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        return MASK + accountNumber.substring(accountNumber.length() - 4);
    }
}