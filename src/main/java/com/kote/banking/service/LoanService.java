package com.kote.banking.service;

import com.kote.banking.dto.LoanRequestDto;
import com.kote.banking.dto.LoanResponseDto;
import com.kote.banking.entity.Account;
import com.kote.banking.entity.Loan;
import com.kote.banking.entity.Transaction;
import com.kote.banking.entity.enums.LoanStatus;
import com.kote.banking.entity.enums.TransactionStatus;
import com.kote.banking.mapper.LoanMapper;
import com.kote.banking.repository.AccountRepository;
import com.kote.banking.repository.LoanRepository;
import com.kote.banking.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanMapper loanMapper;
    private final TransactionRepository transactionRepository;
    public LoanService(LoanRepository loanRepository, AccountRepository accountRepository, LoanMapper loanMapper, TransactionRepository transactionRepository) {
        this.loanRepository = loanRepository;
        this.accountRepository = accountRepository;
        this.loanMapper = loanMapper;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public LoanResponseDto applyLoan(LoanRequestDto loanRequestDto) {
        BigDecimal interestRate = BigDecimal.valueOf(18);
        BigDecimal principalAmount = loanRequestDto.getPrincipalAmount();

        Loan loan = new Loan();

        Account account = accountRepository.findById(loanRequestDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        boolean status = loanRepository.existsLoanByAccountAndLoanStatus(account, LoanStatus.ACTIVE);
        if (status){
            throw new RuntimeException("loan is already used for that account!");
        }
        loan.setAccount(account);
        loan.setPaidAmount(BigDecimal.ZERO);
        loan.setNextPaymentDate(loanRequestDto.getNextPaymentDate());
        loan.setInterestRate(interestRate);
        loan.setTotalAmount(principalAmount);
        loan.setPaidAmount(BigDecimal.ZERO);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        account.setBalance(account.getBalance().add(principalAmount));
        accountRepository.save(account);
        loan.setPrincipalAmount(principalAmount);
        Loan saved = loanRepository.save(loan);

        Transaction transaction = new Transaction();
        transaction.setCreatedAt(Instant.now());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTargetAccountId(account.getAccountNumber());
        transaction.setAmount(principalAmount);
        transaction.setCurrency(account.getCurrency());
        transaction.setReferenceId(UUID.randomUUID().toString());
        transactionRepository.save(transaction);
        return loanMapper.toDto(saved);
    }

    public void calculateInterestRateForActiveLoans() {
        int page = 0;
        int size = 100;
        Page<Loan> loansPage;

        do {
            Pageable pageable = PageRequest.of(page, size);
            loansPage = loanRepository.findLoansByLoanStatus(LoanStatus.ACTIVE, pageable);

            for (Loan loan : loansPage) {
                BigDecimal dailyInterest = loan.getPrincipalAmount()
                        .multiply(loan.getInterestRate())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);

                loan.setTotalAmount(loan.getTotalAmount().add(dailyInterest));
                loanRepository.save(loan);
            }

            page++;
        } while (loansPage.hasNext());
    }
}
