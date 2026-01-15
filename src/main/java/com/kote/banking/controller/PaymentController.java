package com.kote.banking.controller;

import com.kote.banking.entity.ScheduledPayment;
import com.kote.banking.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/scheduled")
    public ResponseEntity<Void> executeScheduledPayments(){
        paymentService.executeScheduledPayments();
        return ResponseEntity.ok().build();    }

    @PostMapping
    public ResponseEntity<ScheduledPayment> processSinglePayment(ScheduledPayment scheduledPayment){
        paymentService.processSinglePayment(scheduledPayment);

        return ResponseEntity.ok().build();
    }
}
