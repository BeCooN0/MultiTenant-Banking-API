package com.kote.banking.controller;

import com.kote.banking.dto.TransactionRequestDto;
import com.kote.banking.dto.TransactionResponseDto;
import com.kote.banking.dto.TransactionSearchDto;
import com.kote.banking.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDto> deposit(@RequestBody TransactionRequestDto transactionRequestDto){
        TransactionResponseDto deposit = transactionService.deposit(transactionRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(deposit.getId())
                .toUri();
        return ResponseEntity.created(location).body(deposit);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(@RequestBody TransactionRequestDto transactionRequestDto){
        TransactionResponseDto withdraw = transactionService.withdraw(transactionRequestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(withdraw.getId())
                .toUri();
        return ResponseEntity.created(location).body(withdraw);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(@RequestBody TransactionRequestDto requestDto){
        TransactionResponseDto transfer = transactionService.transfer(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transfer.getId())
                .toUri();
        return ResponseEntity.created(location).body(transfer);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDto>> getTransactions(@RequestBody TransactionSearchDto dto, @PageableDefault(page = 0, size = 10) Pageable pageable){
        Page<TransactionResponseDto> transactions = transactionService.getTransactions(dto, pageable);
        return ResponseEntity.ok(transactions);
    }

}