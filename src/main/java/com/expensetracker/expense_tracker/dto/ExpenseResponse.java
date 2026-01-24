package com.expensetracker.expense_tracker.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private Long id;
    private Double amount;
    private String category;
    private String description;
    private LocalDate date;
}
