package com.example.financetracker.unit;

import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.TransactionRepository;
import com.example.financetracker.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    void addTransactionWithNullTimeStemp_shouldSaveAndReturnTransaction() {
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
}
