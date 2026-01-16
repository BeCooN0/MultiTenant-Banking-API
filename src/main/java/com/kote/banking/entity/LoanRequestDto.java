package com.kote.banking.entity;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class LoanRequestDto {
    private Long accountId;
    private Long loanProductId;
    private BigDecimal principalAmount;
    private Instant nextPaymentDate;
}