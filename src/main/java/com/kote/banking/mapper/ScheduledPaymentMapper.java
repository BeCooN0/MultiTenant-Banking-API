package com.kote.banking.mapper;

import com.kote.banking.dto.ScheduledPaymentRequestDto;
import com.kote.banking.dto.ScheduledPaymentResponseDto;
import com.kote.banking.entity.ScheduledPayment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScheduledPaymentMapper {

    ScheduledPayment toScheduledPayment(ScheduledPaymentRequestDto dto);
    ScheduledPaymentResponseDto paymentScheduledToDto(ScheduledPayment scheduledPayment);
    void UpdateScheduledPayment(ScheduledPaymentRequestDto requestDto,@MappingTarget ScheduledPayment scheduledPayment);
}
