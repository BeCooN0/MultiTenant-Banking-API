package com.kote.banking.dto;
import com.kote.banking.entity.enums.Currency;
import com.kote.banking.entity.enums.ScheduledFrequency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ScheduledPaymentResponseDto {
    private Long id;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    private Instant executionDate;
    private ScheduledFrequency frequency;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private boolean isActive;
}

