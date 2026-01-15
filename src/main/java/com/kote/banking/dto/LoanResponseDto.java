package com.kote.banking.dto;
import com.kote.banking.entity.enums.LoanStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanResponseDto {
    private Long id;
    private Long accountId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal interestRate;
    private Date nextPaymentDate;
    private LoanStatus loanStatus;
}
