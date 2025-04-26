package com.example.financetracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Category cannot be null")
    @Column(nullable = false)
    private String category;

    @Positive(message = "Amount must be greater than zero")
    @Column(nullable = false)
    private double amount;

    @NotNull(message = "Transaction type cannot be null")
    @Column(nullable = false)
    private String type;

    @NotNull(message = "Timestamp cannot be null")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @NotNull(message = "User cannot be null")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
