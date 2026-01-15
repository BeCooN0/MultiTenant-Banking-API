package com.kote.banking.service;

import com.kote.banking.dto.AccountRequestDto;
import com.kote.banking.dto.AccountResponseDto;
import com.kote.banking.entity.Account;
import com.kote.banking.entity.User;
import com.kote.banking.mapper.AccountMapper;
import com.kote.banking.repository.AccountRepository;
import com.kote.banking.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    public AccountService(AccountMapper accountMapper, AccountRepository accountRepository, UserRepository userRepository) {
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public AccountResponseDto createAccount(AccountRequestDto accountRequestDto){
        Account account = accountMapper.toAccount(accountRequestDto);

        User user = userRepository.findById(accountRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        account.setUser(user);

        if(accountRepository.existsAccountByAccountNumber(account.getAccountNumber())){
            throw new RuntimeException("Account already exists");
        }

        Account saved = accountRepository.save(account);
        return accountMapper.toDto(saved);
    }

    public AccountResponseDto updateAccount(Long accountId,AccountRequestDto accountRequestDto){
        Account account =  accountRepository.findById(accountId).orElseThrow();
        accountMapper.updateAccount(accountRequestDto, account);
        Account saved = accountRepository.save(account);
        return accountMapper.toDto(saved);
    }

    public AccountResponseDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        return accountMapper.toDto(account);
    }

    public boolean deleteAccountById(Long accountId){
        try {
            accountRepository.deleteById(accountId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
