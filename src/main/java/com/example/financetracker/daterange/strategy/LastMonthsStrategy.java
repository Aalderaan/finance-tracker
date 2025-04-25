package com.example.financetracker.daterange.strategy;

import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.financetracker.daterange.DateRangeResolver.DateRange;
import com.example.financetracker.dto.SummaryRequestDTO;

@Component
@Order(6)
public class LastMonthsStrategy implements DateRangeStrategy {
    public boolean supports(SummaryRequestDTO request) {
        return request.getLastMonths() != null;
    }

    public DateRange resolve(SummaryRequestDTO request) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusMonths(request.getLastMonths());
        return new DateRange(from, to);
    }
}
