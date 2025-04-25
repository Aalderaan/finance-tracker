package com.example.financetracker.daterange;

import java.time.LocalDateTime;

public class DateRangeResolver {

    public record DateRange(LocalDateTime from, LocalDateTime to) {}
}