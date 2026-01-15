package com.kote.banking.mapper;

import com.kote.banking.dto.AuthenticationRequestDto;
import com.kote.banking.dto.AuthenticationResponseDto;
import com.kote.banking.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AuthenticationResponseDto toDto(User user);
    User toUser (AuthenticationRequestDto authenticationRequestDto);
    void toUpdate(AuthenticationResponseDto authenticationResponseDto, @MappingTarget User user);
}