package com.example.financetracker.service;

import com.example.financetracker.dto.CategorySummaryDTO;
import com.example.financetracker.dto.SummaryRequestDTO;
import com.example.financetracker.dto.SummaryResponseDTO;
import com.example.financetracker.daterange.DateRangeResolver;
import com.example.financetracker.daterange.strategy.DateRangeStrategy;
import com.example.financetracker.model.User;
import com.example.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    private final List<DateRangeStrategy> strategies;
    private final TransactionRepository transactionRepository;

    public SummaryService(List<DateRangeStrategy> strategies,
                           TransactionRepository transactionRepository) {
        this.strategies = strategies;
        this.transactionRepository = transactionRepository;
    }

    public SummaryResponseDTO getSummary(User user, SummaryRequestDTO request) {
        DateRangeResolver.DateRange range = strategies.stream()
                .filter(strategy -> strategy.supports(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found"))
                .resolve(request);

        if (request.isByCategory()) {
            List<CategorySummaryDTO> byCategory = transactionRepository.getAmountGroupedByCategory(
                    user, request.getType(), range.from(), range.to());

            double total = byCategory.stream()
                    .mapToDouble(CategorySummaryDTO::getTotal)
                    .sum();

            return new SummaryResponseDTO(total, byCategory);
        } else {
            Double total = transactionRepository.getTotalAmountByTypeAndDateRange(
                    user, request.getType(), range.from(), range.to());
            return new SummaryResponseDTO(total != null ? total : 0.0, null);
        }
    }
}
