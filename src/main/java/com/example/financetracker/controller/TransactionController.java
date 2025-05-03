package com.example.financetracker.controller;

import com.example.financetracker.dto.SummaryRequestDTO;
import com.example.financetracker.dto.SummaryResponseDTO;
import com.example.financetracker.dto.TransactionResponseDTO;
import com.example.financetracker.exception.ErrorResponse;
import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.service.AuthenticatedUserService;
import com.example.financetracker.service.SummaryService;
import com.example.financetracker.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final SummaryService summaryService;
    private final AuthenticatedUserService authenticatedUserService;

    public TransactionController(TransactionService transactionService,
                                 SummaryService summaryService,
                                 AuthenticatedUserService authenticatedUserService) {
        this.transactionService = transactionService;
        this.summaryService = summaryService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Operation(summary = "Post a transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction added successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> addTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request) {

        User user = authenticatedUserService.getAuthenticatedUser(userDetails);
        Transaction transaction = transactionService.addTransaction(user, request.category(),
                request.amount(), request.type(), request.timestamp());

        TransactionResponseDTO responseDTO = new TransactionResponseDTO(
                transaction.getId(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Get list of transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Transaction.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticatedUserService.getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(transactionService.getUserTransactions(user));
    }


    @Operation(summary = "Get transaction summary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SummaryResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/summary")
    public ResponseEntity<SummaryResponseDTO> getSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SummaryRequestDTO request) {

        User user = authenticatedUserService.getAuthenticatedUser(userDetails);
        return ResponseEntity.ok(summaryService.getSummary(user, request));
    }

    @Operation(summary = "Update a transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

    @Operation(summary = "Get a transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User user = authenticatedUserService.getAuthenticatedUser(userDetails);
        Transaction transaction = transactionService.getTransactionById(user, id);

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
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
                                     @NotBlank String type, LocalDateTime timestamp) {
    }
}