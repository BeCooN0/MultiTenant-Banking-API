package com.kote.banking.entity;

import com.kote.banking.entity.enums.AccountStatus;
import com.kote.banking.entity.enums.Currency;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private BigDecimal balance;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
}
