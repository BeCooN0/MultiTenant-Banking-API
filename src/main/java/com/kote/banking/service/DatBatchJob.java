package com.kote.banking.service;
import com.kote.banking.repository.TenantRepository;
import com.kote.banking.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class DatBatchJob {
    private final TenantRepository tenantRepository;
    private final LoanService loanService;
    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 0 * * *")
    public void runEndOfDayTask(){
        List<String> allIds = tenantRepository.findAllIds();

        for (String tenantName : allIds) {
            try{
                TenantContext.setTenantName(tenantName);
                log.info("Processing tenant: {}", tenantName);
                try {
                    loanService.calculateInterestRateForActiveLoans();
                } catch (Exception e) {
                    log.info("error calculating interest rating for tenant: " + tenantName + ", " + e.getMessage());
                    throw new RuntimeException("error calculating interest rating for tenant: " + tenantName + ", " + e.getMessage());
                }
                try {
                    paymentService.executeScheduledPayments();
                }catch (RuntimeException e){
                    log.error("Error executing payments for tenant {}: {}", tenantName, e.getMessage());
                }

            }catch (Exception e){
                log.error("Critical error for tenant {}: {}", tenantName, e.getMessage());
            }finally {
                TenantContext.clear();
            }
        }
        log.info("All daily jobs finished!");
    }
}
