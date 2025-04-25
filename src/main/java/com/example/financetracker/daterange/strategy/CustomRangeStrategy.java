package com.example.financetracker.daterange.strategy;

import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.financetracker.daterange.DateRangeResolver.DateRange;
import com.example.financetracker.dto.SummaryRequestDTO;

@Component
@Order(1)
public class CustomRangeStrategy implements DateRangeStrategy {
    public boolean supports(SummaryRequestDTO request) {
        return request.getFrom() != null && request.getTo() != null;
    }

    public DateRange resolve(SummaryRequestDTO request) {
        return new DateRange(
            LocalDateTime.parse(request.getFrom()),
            LocalDateTime.parse(request.getTo())
        );
    }
}
