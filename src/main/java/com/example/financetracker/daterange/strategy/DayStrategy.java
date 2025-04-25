package com.example.financetracker.daterange.strategy;

import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.financetracker.daterange.DateRangeResolver.DateRange;
import com.example.financetracker.dto.SummaryRequestDTO;

@Component
@Order(2)
public class DayStrategy implements DateRangeStrategy {
    public boolean supports(SummaryRequestDTO request) {
        return request.getYear() != null && request.getMonth() != null && request.getDay() != null;
    }

    public DateRange resolve(SummaryRequestDTO request) {
        LocalDateTime from = LocalDateTime.of(request.getYear(), request.getMonth(), request.getDay(), 0, 0);
        return new DateRange(from, from.plusDays(1));
    }
}
