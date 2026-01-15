package com.kote.banking.dto;

import com.kote.banking.entity.enums.TransactionType;
import lombok.Data;

import java.time.Instant;

@Data
public class TransactionSearchDto {
    private String accountNumber;
    private Instant createdAt;
    private Instant endDate;
    private TransactionType transactionType;
    private Integer page;
    private Integer size;
}