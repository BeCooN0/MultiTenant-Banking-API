package com.kote.banking.dto;

import com.kote.banking.entity.enums.AccountStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountResponseDto {
    private Long id;
    private String accountNumber;
    private Long userId;
    private AccountStatus accountStatus;
    private BigDecimal balance;
}