package com.example.financetracker.service;

import com.example.financetracker.daterange.DateRangeResolver;
import com.example.financetracker.dto.SummaryRequestDTO;
import com.example.financetracker.dto.SummaryResponseDTO;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.TransactionRepository;
import com.example.financetracker.daterange.strategy.DateRangeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SummaryServiceTest {

    private TransactionRepository transactionRepository;
    private SummaryService summaryService;
    private DateRangeStrategy dateRangeStrategy;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        dateRangeStrategy = mock(DateRangeStrategy.class);
        summaryService = new SummaryService(List.of(dateRangeStrategy), transactionRepository);
    }

    @Test
    void getSummary_totalAmount_shouldReturnTotal() {
        User user = User.builder().id(1L).username("testuser").build();

        SummaryRequestDTO request = SummaryRequestDTO
                .builder()
                .type("EXPENSE")
                .year(2025)
                .byCategory(false)
                .build();

        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 23, 59);

        when(dateRangeStrategy.supports(request)).thenReturn(true);
        when(dateRangeStrategy.resolve(request)).thenReturn(new DateRangeResolver.DateRange(from, to));
        when(transactionRepository.getTotalAmountByTypeAndDateRange(user, "EXPENSE", from, to)).thenReturn(500.0);

        SummaryResponseDTO summary = summaryService.getSummary(user, request);

        assertNotNull(summary);
        assertEquals(500.0, summary.getTotal());
        assertNull(summary.getByCategory());
    }
}
