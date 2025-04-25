package com.example.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategorySummaryDTO {
    private String category;
    private Double total;
}

