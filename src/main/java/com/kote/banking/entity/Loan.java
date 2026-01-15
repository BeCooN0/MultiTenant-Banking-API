package com.kote.banking.entity;

import com.kote.banking.entity.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Account account;
    private BigDecimal totalAmount;
    private BigDecimal principalAmount;
    private BigDecimal paidAmount;
    private BigDecimal interestRate;
    private Date nextPaymentDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;
}
