package com.kote.banking.service;

import com.kote.banking.dto.TransactionRequestDto;
import com.kote.banking.dto.TransactionResponseDto;
import com.kote.banking.dto.TransactionSearchDto;
import com.kote.banking.entity.Account;
import com.kote.banking.entity.Transaction;
import com.kote.banking.entity.enums.TransactionStatus;
import com.kote.banking.entity.enums.TransactionType;
import com.kote.banking.mapper.TransactionMapper;
import com.kote.banking.repository.AccountRepository;
import com.kote.banking.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService {
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionSpecification transactionSpecification;

    public TransactionService(TransactionMapper transactionMapper, TransactionRepository transactionRepository, AccountRepository accountRepository, TransactionSpecification transactionSpecification) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionSpecification = transactionSpecification;
    }

    @Transactional
    public TransactionResponseDto deposit(TransactionRequestDto transactionRequestDto) {
        if (transactionRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("deposit amount should be greater than 0!");
        }

        Account targetAccount = accountRepository.findByAccountNumberWithLock(transactionRequestDto.getTargetAccountId()).orElseThrow();
        if (!targetAccount.getCurrency().equals(transactionRequestDto.getCurrency())) {
            throw new RuntimeException("only " + targetAccount.getCurrency() + " currency should be allowed!");
        }
        targetAccount.setBalance(targetAccount.getBalance().add(transactionRequestDto.getAmount()));
        accountRepository.save(targetAccount);
        Transaction saved = createTransaction(targetAccount, transactionRequestDto.getAmount(), TransactionType.DEPOSIT);
        return transactionMapper.toDto(saved);
    }

    @Transactional
    public TransactionResponseDto withdraw(TransactionRequestDto transactionRequestDto) {
        if (transactionRequestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be > 0");
        }
        BigDecimal requiredWithdrawAmount = transactionRequestDto.getAmount();
        Account targetAccount = accountRepository.findByAccountNumberWithLock(transactionRequestDto.getTargetAccountId()).orElseThrow();
        if (!targetAccount.getCurrency().equals(transactionRequestDto.getCurrency())) {
            throw new RuntimeException("only " + transactionRequestDto.getCurrency() + " is allow!");
        }
        if (requiredWithdrawAmount.compareTo(targetAccount.getBalance()) > 0) {
            throw new RuntimeException("Not enough amount!");
        }
        BigDecimal remainsBalance = targetAccount.getBalance().subtract(requiredWithdrawAmount);
        targetAccount.setBalance(remainsBalance);
        accountRepository.save(targetAccount);
        Transaction saved = createTransaction(targetAccount, requiredWithdrawAmount, TransactionType.WITHDRAW);
        return transactionMapper.toDto(saved);
    }

    private Transaction createTransaction(Account account, BigDecimal amount,
                                          TransactionType transactionType) {
        Transaction transaction = new Transaction();
        transaction.setTargetAccountId(account.getAccountNumber());
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setCreatedAt(Instant.now());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCurrency(account.getCurrency());
        transaction.setReferenceId(UUID.randomUUID().toString());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public TransactionResponseDto transfer(TransactionRequestDto request) {
        String sourceId = request.getSourceAccountId();
        String targetId = request.getTargetAccountId();
        BigDecimal amount = request.getAmount();

        if (sourceId.equals(targetId)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        boolean lockSourceFirst = sourceId.compareTo(targetId) < 0;

        Account sourceAccount;
        Account targetAccount;

        if (lockSourceFirst) {
            sourceAccount = accountRepository.findByAccountNumberWithLock(sourceId)
                    .orElseThrow(() -> new RuntimeException("Source account not found"));
            targetAccount = accountRepository.findByAccountNumberWithLock(targetId)
                    .orElseThrow(() -> new RuntimeException("Target account not found"));
        } else {
            targetAccount = accountRepository.findByAccountNumberWithLock(targetId)
                    .orElseThrow(() -> new RuntimeException("Target account not found"));
            sourceAccount = accountRepository.findByAccountNumberWithLock(sourceId)
                    .orElseThrow(() -> new RuntimeException("Source account not found"));
        }


        if (!sourceAccount.getCurrency().equals(request.getCurrency())) {
            throw new RuntimeException("Source account currency mismatch!");
        }
        if (!targetAccount.getCurrency().equals(request.getCurrency())) {
            throw new RuntimeException("Target account currency mismatch!");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Not enough balance!");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction transaction = new Transaction();
        transaction.setReferenceId(UUID.randomUUID().toString());
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setCreatedAt(Instant.now());
        transaction.setCurrency(request.getCurrency());
        transaction.setSourceAccountId(sourceId);
        transaction.setTargetAccountId(targetId);
        transaction.setStatus(TransactionStatus.SUCCESS);

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDto(saved);
    }


    public Page<TransactionResponseDto> getTransactions(TransactionSearchDto dto, Pageable pageable){
        Specification<Transaction> transactions = transactionSpecification.getTransactions(dto);
        Page<Transaction> all = transactionRepository.findAll(transactions, pageable);
        return all.map(transactionMapper::toDto);
    }
}