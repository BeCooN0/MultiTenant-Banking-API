package com.kote.banking.repository;

import com.kote.banking.entity.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduledPayment, Long> {
    List<ScheduledPayment> findAllByNextPaymentDateLessThanEqualAndIsActive(Instant now, boolean isActive);
}
