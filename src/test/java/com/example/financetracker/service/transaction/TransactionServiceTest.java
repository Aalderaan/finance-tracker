package com.example.financetracker.service.transaction;

import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.TransactionRepository;
import com.example.financetracker.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {
    private TransactionRepository transactionRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository);
    }

    @Test
    void addTransaction_shouldSaveAndReturnTransaction() {
        User user = User.builder().id(1L).username("john").build();
        String category = "Food";
        double amount = 50.0;
        String type = "EXPENSE";
        LocalDateTime timeStamp = LocalDate.now().minusDays(1).atStartOfDay();

        Transaction mockTransaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(amount)
                .type(type)
                .timestamp(timeStamp)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        Transaction result = transactionService.addTransaction(user, category, amount, type, timeStamp);

        assertNotNull(result);
        assertEquals(category, result.getCategory());
        assertEquals(amount, result.getAmount());
        assertEquals(type, result.getType());
        assertEquals(timeStamp, result.getTimestamp());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void addTransactionWithNullTimeStamp_shouldSaveAndReturnTransaction() {
        User user = User.builder().id(1L).username("john").build();
        String category = "Food";
        double amount = 50.0;
        String type = "EXPENSE";
        LocalDateTime timeStamp = LocalDate.now().atStartOfDay();

        Transaction mockTransaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(amount)
                .type(type)
                .timestamp(timeStamp)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        Transaction result = transactionService.addTransaction(user, category, amount, type, null);

        assertNotNull(result);
        assertEquals(category, result.getCategory());
        assertEquals(amount, result.getAmount());
        assertEquals(type, result.getType());
        assertEquals(timeStamp, result.getTimestamp());


        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
    
    @Test
    void getUserTransactions_shouldReturnTransactionsForUser() {
        User user = User.builder().id(1L).username("john").build();
        Transaction tx1 = Transaction.builder().category("Food").amount(20).type("EXPENSE").build();
        Transaction tx2 = Transaction.builder().category("Salary").amount(2000).type("INCOME").build();

        when(transactionRepository.findByUser(user)).thenReturn(Arrays.asList(tx1, tx2));

        List<Transaction> result = transactionService.getUserTransactions(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(tx1));
        assertTrue(result.contains(tx2));
    }
    
    @Test
    void updateTransaction_shouldUpdateExistingTransaction() {
        Long transactionId = 1L;
        Transaction existing = Transaction.builder()
                .id(transactionId)
                .category("Food")
                .amount(20.0)
                .type("EXPENSE")
                .timestamp(LocalDateTime.now())
                .build();

        Transaction updated = Transaction.builder()
                .category("Transport")
                .amount(50.0)
                .type("EXPENSE")
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existing));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(existing);

        Transaction result = transactionService.updateTransaction(transactionId, updated.getCategory(), 
        		updated.getAmount(), updated.getType(), updated.getTimestamp());

        assertEquals("Transport", result.getCategory());
        assertEquals(50.0, result.getAmount());
        assertEquals(existing.getType(), result.getType());
        assertEquals(existing.getTimestamp(), result.getTimestamp());
        verify(transactionRepository, times(1)).save(existing);
    }

    @Test
    void deleteTransaction_shouldDeleteTransactionById() {
        Long transactionId = 2L;
        Transaction existing = Transaction.builder()
                .id(transactionId)
                .category("Food")
                .amount(35.0)
                .type("EXPENSE")
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existing));

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).delete(existing); 
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void updateTransaction_shouldThrowException_whenTransactionNotFound() {
        Long transactionId = 100L;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                transactionService.updateTransaction(transactionId, "Transport", 100.0, "EXPENSE", LocalDateTime.now()));

        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void deleteTransaction_shouldThrowException_whenTransactionNotFound() {
        Long transactionId = 100L;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                transactionService.deleteTransaction(transactionId));

        verify(transactionRepository, times(1)).findById(transactionId);
    }
}
