package com.example.financetracker.service;

import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction addTransaction(User user, String category, double amount, String type, LocalDateTime timestamp) {
        Transaction t = Transaction.builder()
                .user(user)
                .category(category)
                .amount(amount)
                .type(type)
                .timestamp(timestamp != null ? timestamp : LocalDateTime.now())
                .build();
        return transactionRepository.save(t);
    }

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }
}