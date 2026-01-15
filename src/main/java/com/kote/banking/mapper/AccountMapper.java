package com.kote.banking.mapper;

import com.kote.banking.dto.AccountRequestDto;
import com.kote.banking.dto.AccountResponseDto;
import com.kote.banking.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "userId", source = "user.id")
    AccountResponseDto toDto(Account account);

    @Mapping(target = "user.id", source = "userId")
    Account toAccount(AccountRequestDto accountRequestDto);

    @Mapping(target = "user.id", source = "userId")
    void updateAccount(AccountRequestDto request, @MappingTarget Account account);
}