package com.kote.banking.entity;

import com.kote.banking.entity.enums.Currency;
import com.kote.banking.entity.enums.TransactionStatus;
import com.kote.banking.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private Instant createdAt;
    private String referenceId;
    private TransactionType transactionType;
}
