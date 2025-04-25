package com.example.financetracker.dto;

import lombok.Data;

@Data
public class SummaryRequestDTO {
    private String type;
    private Integer year;
    private Integer month;
    private Integer day;
    private String from;
    private String to;
    private Integer lastDays;
    private Integer lastMonths;
    private boolean byCategory;
}
