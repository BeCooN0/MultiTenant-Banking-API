package com.kote.banking.controller;

import com.kote.banking.service.DatBatchJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class JobController {
    private final DatBatchJob datBatchJob;

    @GetMapping("/force-run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> forceRunEndOfDayJob() {
        datBatchJob.runEndOfDayTask();
        return ResponseEntity.ok("Daily job executed manually successfully!");
    }
}