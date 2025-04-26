package com.example.financetracker.controller;

import com.example.financetracker.dto.SummaryRequestDTO;
import com.example.financetracker.dto.SummaryResponseDTO;
import com.example.financetracker.dto.TransactionResponseDTO;
import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.service.SummaryService;
import com.example.financetracker.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<TransactionResponseDTO> addTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found in the database"));

        Transaction transaction = transactionService.addTransaction(user, request.category(), request.amount(), request.type(), request.timestamp());

        TransactionResponseDTO responseDTO = new TransactionResponseDTO(
                transaction.getId(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return ResponseEntity.ok(transactionService.getUserTransactions(user));
    }
    
    @PostMapping("/summary")
    public ResponseEntity<SummaryResponseDTO> getSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SummaryRequestDTO request) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseEntity.ok(summaryService.getSummary(user, request));
    }
    
    @Operation(summary = "Update a transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(
                name = "transactionId",
                description = "ID of the transaction to update",
                required = true,
                in = ParameterIn.PATH
            )
            @PathVariable("transactionId") Long transactionId,
            @Valid @RequestBody TransactionRequest request) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found in the database"));

        Transaction transaction = transactionService.updateTransaction(
                transactionId,
                request.category(),
                request.amount(),
                request.type(),
                request.timestamp()
        );

        TransactionResponseDTO responseDTO = new TransactionResponseDTO(
                transaction.getId(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Delete a transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(
                    name = "transactionId",
                    description = "ID of the transaction",
                    required = true,
                    in = ParameterIn.PATH
                )
            
            @PathVariable("transactionId") Long transactionId) {

        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }


    public record TransactionRequest(@NotBlank String category, @Positive double amount, 
    		@NotBlank String type, LocalDateTime timestamp) {}
}