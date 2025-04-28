package com.example.financetracker.controller;

import com.example.financetracker.dto.TransactionResponseDTO;
import com.example.financetracker.model.Transaction;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.service.AuthenticatedUserService;
import com.example.financetracker.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void addTransaction_shouldReturnTransaction() {
        User user = User.builder().id(1L).username("testUser").build();
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn("testUser");
        when(authenticatedUserService.getAuthenticatedUser(userDetails)).thenReturn(user);

        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(user)
                .amount(100.0)
                .category("Food")
                .type("EXPENSE")
                .timestamp(LocalDateTime.now())
                .build();

        when(transactionService.addTransaction(any(), anyString(), anyDouble(), anyString(), any()))
                .thenReturn(transaction);

        TransactionController.TransactionRequest request = new TransactionController.TransactionRequest("Food", 100.0, "EXPENSE", null);

        ResponseEntity<TransactionResponseDTO> response = transactionController.addTransaction(userDetails, request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("Food", response.getBody().getCategory());
        assertEquals(100.0, response.getBody().getAmount());
        assertEquals("EXPENSE", response.getBody().getType());
    }

    @Test
    void getTransactions_shouldReturnListOfTransactions() {
        User user = User.builder().username("testUser").build();

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
        when(userDetails.getUsername()).thenReturn("testUser");
        when(authenticatedUserService.getAuthenticatedUser(userDetails)).thenReturn((user));
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

    @Test
    void updateTransaction_shouldReturnUpdatedTransaction() {
        User user = User.builder().username("testUser").build();
        Transaction updatedTransaction = Transaction.builder()
                .category("Updated")
                .amount(300.0)
                .type("INCOME")
                .timestamp(LocalDateTime.now())
                .build();

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(authenticatedUserService.getAuthenticatedUser(userDetails)).thenReturn(user);
        when(transactionService.updateTransaction(eq(1L), anyString(), anyDouble(), anyString(), any()))
                .thenReturn(updatedTransaction);

        TransactionController.TransactionRequest request =
                new TransactionController.TransactionRequest("Updated", 300.0, "INCOME", LocalDateTime.now());

        ResponseEntity<TransactionResponseDTO> response = transactionController.updateTransaction(userDetails, 1L, request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("Updated", response.getBody().getCategory());
    }

    @Test
    void deleteTransaction_shouldDeleteTransactionSuccessfully() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        ResponseEntity<Void> response = transactionController.deleteTransaction(userDetails, 1L);

        assertEquals(204, response.getStatusCode().value());
        verify(transactionService, times(1)).deleteTransaction(1L);
    }

}
