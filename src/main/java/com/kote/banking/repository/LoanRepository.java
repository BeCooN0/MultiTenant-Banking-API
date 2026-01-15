package com.kote.banking.repository;

import com.kote.banking.entity.Account;
import com.kote.banking.entity.Loan;
import com.kote.banking.entity.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // დაემატა ეს იმპორტი
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsLoanByAccountAndLoanStatus(Account account, LoanStatus loanStatus);

    Page<Loan> findLoansByLoanStatus(LoanStatus loanStatus, Pageable pageable);
}