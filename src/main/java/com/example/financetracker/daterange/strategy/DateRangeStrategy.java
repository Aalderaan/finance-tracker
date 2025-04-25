package com.example.financetracker.daterange.strategy;

import com.example.financetracker.dto.SummaryRequestDTO;
import com.example.financetracker.daterange.DateRangeResolver.DateRange;

public interface DateRangeStrategy {
    boolean supports(SummaryRequestDTO request);
    DateRange resolve(SummaryRequestDTO request);
}
