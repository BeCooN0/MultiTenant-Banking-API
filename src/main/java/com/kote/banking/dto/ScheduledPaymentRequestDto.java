package com.kote.banking.dto;

import com.kote.banking.entity.enums.Currency;
import com.kote.banking.entity.enums.ScheduledFrequency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ScheduledPaymentRequestDto {
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal PaymentAmount;
    private Instant executionTime;
    private ScheduledFrequency frequency;
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    private Currency currency;

}

