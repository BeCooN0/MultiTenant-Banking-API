package com.kote.banking.dto;

import com.kote.banking.entity.enums.Currency;
import com.kote.banking.entity.enums.TransactionStatus;
import com.kote.banking.entity.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionResponseDto {
    private Long id;
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionStatus status;
    private Instant createdAt;
    private String referenceId;
    private TransactionType transactionType;

}
