package com.example.financetracker.unit;

import com.example.financetracker.controller.TransactionController;
import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTransaction_shouldReturnTransaction() {
        User user = User.builder().username("testuser").build();
        Transaction transaction = Transaction.builder().user(user).amount(100.0).category("Food").type("EXPENSE").build();
        TransactionController.TransactionRequest request =
                new TransactionController.TransactionRequest("Food", 100.0, "EXPENSE");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionService.addTransaction(user, "Food", 100.0, "EXPENSE")).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.addTransaction(userDetails, request);

        assertEquals(true, response.getStatusCode().is2xxSuccessful());
        assertEquals(transaction, response.getBody());
    }

    @Test
    void getTransactions_shouldReturnListOfTransactions() {
        User user = User.builder().username("testuser").build();
        Transaction transaction = Transaction.builder().user(user).amount(100.0).category("Food").type("EXPENSE").build();

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionService.getUserTransactions(user)).thenReturn(Collections.singletonList(transaction));

        ResponseEntity<?> response = transactionController.getTransactions(userDetails);

        assertEquals(true, response.getStatusCode().is2xxSuccessful());
        assertTrue(((java.util.List<?>) response.getBody()).contains(transaction));
    }
}
