package com.expensetracker.expense_tracker.currency;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for external exchange rate API.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateApiResponse {
    @JsonProperty("base")
    private String base;
    
    @JsonProperty("date")
    private String date;
    
    @JsonProperty("rates")
    private Map<String, Double> rates;
}
