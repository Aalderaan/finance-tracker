package com.example.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SummaryResponseDTO {
    private Double total;
    private List<CategorySummaryDTO> byCategory;
}
