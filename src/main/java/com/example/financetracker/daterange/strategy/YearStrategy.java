package com.example.financetracker.daterange.strategy;

import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.financetracker.daterange.DateRangeResolver.DateRange;
import com.example.financetracker.dto.SummaryRequestDTO;

@Component
@Order(4)
public class YearStrategy implements DateRangeStrategy {
    public boolean supports(SummaryRequestDTO request) {
        return request.getYear() != null && request.getMonth() == null && request.getDay() == null;
    }

    public DateRange resolve(SummaryRequestDTO request) {
        LocalDateTime from = LocalDateTime.of(request.getYear(), 1, 1, 0, 0);
        return new DateRange(from, from.plusYears(1));
    }
}

