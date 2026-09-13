package org.lvmp.statementanalysis_springboot.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lvmp.statementanalysis_springboot.account.internal.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/account/v1")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts() {
        return accountService.getAccounts();
    }

    @PostMapping("/create-account")
    public ResponseEntity<Void> createAccount(@Valid @RequestBody AccountRequest request) {
        return accountService.createAccount(request);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Void> updateAccount(@PathVariable UUID accountId, @Valid @RequestBody AccountRequest request) {
        return accountService.updateAccount(accountId, request);
    }
}