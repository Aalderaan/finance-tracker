package com.example.financetracker.controller;

import com.example.financetracker.dto.SummaryRequestDTO;
import com.example.financetracker.dto.SummaryResponseDTO;
import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.service.SummaryService;
import com.example.financetracker.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final SummaryService summaryService;

    public TransactionController(TransactionService transactionService, UserRepository userRepository, SummaryService summaryService) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.summaryService = summaryService;
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody TransactionRequest request) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(
                transactionService.addTransaction(user, request.category(), request.amount(), request.type()));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(transactionService.getUserTransactions(user));
    }
    
    @PostMapping("/summary")
    public ResponseEntity<SummaryResponseDTO> getSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SummaryRequestDTO request) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        return ResponseEntity.ok(summaryService.getSummary(user, request));
    }


    public record TransactionRequest(String category, double amount, String type) {}
}