package com.kote.banking.controller;

import com.kote.banking.dto.AccountRequestDto;
import com.kote.banking.dto.AccountResponseDto;
import com.kote.banking.dto.TransactionResponseDto;
import com.kote.banking.dto.TransactionSearchDto;
import com.kote.banking.entity.enums.TransactionType;
import com.kote.banking.service.AccountService;
import com.kote.banking.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;
    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostMapping()
    public ResponseEntity<AccountResponseDto> createAccount(@RequestBody AccountRequestDto accountRequestDto){
        AccountResponseDto account = accountService.createAccount(accountRequestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(account.getId())
                .toUri();

        return ResponseEntity.created(location).body(account);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> updateAccount(@PathVariable Long id, @RequestBody AccountRequestDto accountRequestDto){
        AccountResponseDto accountResponseDto = accountService.updateAccount(id, accountRequestDto);
        return ResponseEntity.ok(accountResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountById(@PathVariable Long id){
        try {
            accountService.deleteAccountById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/statement")
    public ResponseEntity<Page<TransactionResponseDto>> getAccountStatement(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTo,
            @RequestParam(required = false) TransactionType transactionType,
            Pageable pageable
    ) {
        AccountResponseDto accountDto = accountService.getAccountById(id);

        TransactionSearchDto searchDto = new TransactionSearchDto();
        searchDto.setAccountNumber(accountDto.getAccountNumber());
        searchDto.setCreatedAt(dateFrom);
        searchDto.setEndDate(dateTo);
        searchDto.setTransactionType(transactionType);

        return ResponseEntity.ok(transactionService.getTransactions(searchDto, pageable));
    }
}