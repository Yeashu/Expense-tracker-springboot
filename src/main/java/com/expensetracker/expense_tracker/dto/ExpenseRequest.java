package com.expensetracker.expense_tracker.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequest {
    
    @NotNull
    private Double amount;

    @NotBlank
    private String category;

    private String description;

    @NotNull
    private LocalDate date;
}
