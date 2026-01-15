package com.kote.banking.service;

import com.kote.banking.dto.TransactionRequestDto;
import com.kote.banking.entity.ScheduledPayment;
import com.kote.banking.entity.enums.TransactionType;
import com.kote.banking.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class PaymentService {

    private final TransactionService transactionService;
    private final ScheduleRepository scheduleRepository;

    public PaymentService(TransactionService transactionService, ScheduleRepository scheduleRepository) {
        this.transactionService = transactionService;
        this.scheduleRepository = scheduleRepository;
    }

    public void executeScheduledPayments() {
        List<ScheduledPayment> all = scheduleRepository.findAllByNextPaymentDateLessThanEqualAndIsActive(Instant.now(), true);

        for (ScheduledPayment payment : all) {
            try {
                processSinglePayment(payment);
            } catch (RuntimeException e) {
                log.error("Error executing payment {}: {}", payment.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void processSinglePayment(ScheduledPayment scheduledPayment) {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setAmount(scheduledPayment.getAmount());
        requestDto.setSourceAccountId(scheduledPayment.getSourceAccountNumber());
        requestDto.setTargetAccountId(scheduledPayment.getTargetAccountNumber());
        requestDto.setTransactionType(TransactionType.TRANSFER);
        requestDto.setCurrency(scheduledPayment.getCurrency());

        transactionService.transfer(requestDto);

        ZonedDateTime nextDate = scheduledPayment.getNextPaymentDate()
                .atZone(ZoneId.systemDefault());

        nextDate = switch (scheduledPayment.getFrequency()) {
            case WEEKLY -> nextDate.plusWeeks(1);
            case MONTHLY -> nextDate.plusMonths(1);
            case YEARLY -> nextDate.plusYears(1);
            default -> nextDate;
        };

        scheduledPayment.setNextPaymentDate(nextDate.toInstant());
        scheduleRepository.save(scheduledPayment);

        log.info("Scheduled payment executed for account: {}", scheduledPayment.getSourceAccountNumber());
    }
}