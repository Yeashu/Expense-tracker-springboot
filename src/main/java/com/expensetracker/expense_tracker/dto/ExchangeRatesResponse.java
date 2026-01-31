package com.expensetracker.expense_tracker.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRatesResponse {
    private String base;
    private String date;
    private Map<String, Double> rates;
}
