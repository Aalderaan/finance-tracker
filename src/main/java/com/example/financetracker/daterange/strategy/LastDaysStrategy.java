package com.example.financetracker.daterange.strategy;

import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.financetracker.daterange.DateRangeResolver.DateRange;
import com.example.financetracker.dto.SummaryRequestDTO;

@Component
@Order(5)
public class LastDaysStrategy implements DateRangeStrategy {
    public boolean supports(SummaryRequestDTO request) {
        return request.getLastDays() != null;
    }

    public DateRange resolve(SummaryRequestDTO request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusDays(request.getLastDays());
        return new DateRange(from, to);
    }
}
