package com.example.financetracker.dto;

import lombok.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private String category;
    private double amount;
    private String type;
    private LocalDateTime timestamp;
}
