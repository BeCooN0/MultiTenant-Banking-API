package com.kote.banking.entity;

import com.kote.banking.entity.enums.Currency;
import com.kote.banking.entity.enums.ScheduledFrequency;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
public class ScheduledPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Instant nextPaymentDate;

    @Enumerated(EnumType.STRING)
    private ScheduledFrequency frequency;

    private boolean isActive;
}