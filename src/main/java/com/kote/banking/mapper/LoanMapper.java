package com.kote.banking.mapper;

import com.kote.banking.dto.LoanRequestDto;
import com.kote.banking.dto.LoanResponseDto;
import com.kote.banking.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "account.id", source = "accountId")
    Loan toLoan(LoanRequestDto loanRequestDto);
    @Mapping(target = "accountId", source = "account.id")
    LoanResponseDto toDto(Loan loan);

    @Mapping(target = "account.id", source = "accountId")
    void updateLoan(LoanRequestDto loanRequestDto, @MappingTarget Loan loan);
}