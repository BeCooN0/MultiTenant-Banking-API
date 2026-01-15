package com.kote.banking.dto;

import com.kote.banking.entity.enums.AccountStatus;
import com.kote.banking.entity.enums.Currency;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class AccountRequestDto {
    private String accountNumber;
    private Long userId;
    private Instant createdAt;
    private AccountStatus status;
    private boolean isActive;
    private BigDecimal balance;
    private Currency currency;
}