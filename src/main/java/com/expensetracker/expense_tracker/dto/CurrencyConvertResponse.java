package com.expensetracker.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConvertResponse {
    private Double amount;
    private String from;
    private String to;
    private Double convertedAmount;
    private Double exchangeRate;
}
