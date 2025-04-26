package com.example.financetracker.unit;

import com.example.financetracker.controller.TransactionController;
import com.example.financetracker.dto.TransactionResponseDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
        LocalDateTime timeStamp = LocalDate.now().atStartOfDay();
        Transaction transaction = Transaction.builder().user(user).amount(100.0).category("Food").type("EXPENSE")
        		.timestamp(timeStamp).build();
        TransactionController.TransactionRequest request =
                new TransactionController.TransactionRequest("Food", 100.0, "EXPENSE", null);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionService.addTransaction(user, "Food", 100.0, "EXPENSE", null)).thenReturn(transaction);

        ResponseEntity<TransactionResponseDTO> response = transactionController.addTransaction(userDetails, request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        TransactionResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Food", responseBody.getCategory());
        assertEquals(100.0, responseBody.getAmount());
        assertEquals("EXPENSE", responseBody.getType());
        assertEquals(transaction.getTimestamp(), responseBody.getTimestamp());
    }

    @Test
    void getTransactions_shouldReturnListOfTransactions() {
        User user = User.builder().username("testuser").build();

        Transaction transaction1 = Transaction.builder()
                .user(user)
                .amount(100.0)
                .category("Food")
                .type("EXPENSE")
                .timestamp(LocalDateTime.now())
                .build();

        Transaction transaction2 = Transaction.builder()
                .user(user)
                .amount(200.0)
                .category("Transport")
                .type("EXPENSE")
                .timestamp(LocalDateTime.now())
                .build();

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionService.getUserTransactions(user)).thenReturn(List.of(transaction1, transaction2));

        ResponseEntity<List<Transaction>> response = transactionController.getTransactions(userDetails);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        Transaction result1 = response.getBody().get(0);
        Transaction result2 = response.getBody().get(1);

        assertEquals(transaction1.getCategory(), result1.getCategory());
        assertEquals(transaction1.getAmount(), result1.getAmount());
        assertEquals(transaction1.getType(), result1.getType());
        assertEquals(transaction1.getTimestamp(), result1.getTimestamp());

        assertEquals(transaction2.getCategory(), result2.getCategory());
        assertEquals(transaction2.getAmount(), result2.getAmount());
        assertEquals(transaction2.getType(), result2.getType());
        assertEquals(transaction2.getTimestamp(), result2.getTimestamp());
    }
}
