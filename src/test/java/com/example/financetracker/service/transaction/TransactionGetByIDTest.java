package com.example.financetracker.service.transaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.TransactionRepository;
import com.example.financetracker.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransactionGetByIDTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        transaction = new Transaction();
        transaction.setId(100L);
        transaction.setCategory("Food");
        transaction.setAmount(50.0);
        transaction.setType("Expense");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(user);
    }

    @Test
    void testGetTransactionById_Success() {
        when(transactionRepository.findById(100L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(user, 100L);

        assertNotNull(result);
        assertEquals(transaction.getId(), result.getId());
        assertEquals(transaction.getUser().getId(), user.getId());
    }

    @Test
    void testGetTransactionById_NotFound() {
        when(transactionRepository.findById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> transactionService.getTransactionById(user, 100L)
        );

        assertEquals("Transaction not found with id: 100", exception.getMessage());
    }

    @Test
    void testGetTransactionById_UserMismatch() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(transaction));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> transactionService.getTransactionById(anotherUser, 100L)
        );

        assertEquals("You are not allowed to access this transaction", exception.getMessage());
    }
}