package com.example.financetracker.daterange.strategy;

import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.financetracker.daterange.DateRangeResolver.DateRange;
import com.example.financetracker.dto.SummaryRequestDTO;

@Component
@Order(6)
public class DefaultStrategy implements DateRangeStrategy {
    public boolean supports(SummaryRequestDTO request) {
        return true;
    }

    public DateRange resolve(SummaryRequestDTO request) {
        return new DateRange(LocalDateTime.MIN, LocalDateTime.now());
    }
}
