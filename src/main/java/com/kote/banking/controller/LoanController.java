package com.kote.banking.controller;


import com.kote.banking.dto.LoanRequestDto;
import com.kote.banking.dto.LoanResponseDto;
import com.kote.banking.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController()
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public ResponseEntity<LoanResponseDto> applyLoan(@RequestBody LoanRequestDto loanRequestDto){
        LoanResponseDto loanResponseDto = loanService.applyLoan(loanRequestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(loanResponseDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(loanResponseDto);
    }

}
