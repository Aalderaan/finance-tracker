package com.example.financetracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryRequestDTO {

    @NotBlank(message = "Transaction type must not be blank")
    private String type;

    @Min(value = 2000, message = "Year must be after 2000")
    private Integer year;

    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @Min(value = 1, message = "Day must be between 1 and 31")
    @Max(value = 31, message = "Day must be between 1 and 31")
    private Integer day;

    private String from;
    private String to;

    @Positive(message = "Last days must be greater than zero")
    private Integer lastDays;

    @Positive(message = "Last months must be greater than zero")
    private Integer lastMonths;

    private boolean byCategory;
}
