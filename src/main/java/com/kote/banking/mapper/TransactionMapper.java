package com.kote.banking.mapper;

import com.kote.banking.dto.TransactionRequestDto;
import com.kote.banking.dto.TransactionResponseDto;
import com.kote.banking.entity.Transaction;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toTransaction(TransactionRequestDto requestDro);
    TransactionResponseDto toDto(Transaction transaction);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUpdate(TransactionRequestDto requestDro, @MappingTarget Transaction transaction);
}
