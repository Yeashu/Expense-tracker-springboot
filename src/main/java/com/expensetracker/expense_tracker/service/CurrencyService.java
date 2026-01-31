package com.expensetracker.expense_tracker.service;

import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.expensetracker.expense_tracker.config.CurrencyConfig;
import com.expensetracker.expense_tracker.currency.ExchangeRateApiResponse;
import com.expensetracker.expense_tracker.currency.ExchangeRateClient;
import com.expensetracker.expense_tracker.dto.CurrencyConvertResponse;
import com.expensetracker.expense_tracker.dto.ExchangeRatesResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for currency conversion operations.
 * Caches exchange rates to minimize external API calls.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    
    private final ExchangeRateClient exchangeRateClient;
    private final CurrencyConfig currencyConfig;
    
    public CurrencyConvertResponse convertCurrency(Double amount, String from, String to) {
        if (amount == null || amount < 0) {
            throw new java.lang.IllegalArgumentException("Amount must be a positive number");
        }
        
        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new java.lang.IllegalArgumentException("Currency codes cannot be empty");
        }
        
        from = from.toUpperCase();
        to = to.toUpperCase();
        
        if (from.equals(to)) {
            return CurrencyConvertResponse.builder()
                    .amount(amount)
                    .from(from)
                    .to(to)
                    .convertedAmount(amount)
                    .exchangeRate(1.0)
                    .build();
        }
        
        Map<String, Double> rates = getExchangeRates(from).getRates();
        
        if (!rates.containsKey(to)) {
            throw new java.lang.IllegalArgumentException("Target currency not supported: " + to);
        }
        
        Double exchangeRate = rates.get(to);
        Double convertedAmount = amount * exchangeRate;
        
        return CurrencyConvertResponse.builder()
                .amount(amount)
                .from(from)
                .to(to)
                .convertedAmount(convertedAmount)
                .exchangeRate(exchangeRate)
                .build();
    }
    
    @Cacheable(value = "exchangeRates", key = "#baseCurrency")
    public ExchangeRatesResponse getExchangeRates(String baseCurrency) {
        if (baseCurrency == null || baseCurrency.isBlank()) {
            baseCurrency = currencyConfig.getBase();
        }
        
        baseCurrency = baseCurrency.toUpperCase();
        
        log.debug("Fetching exchange rates for base currency: {}", baseCurrency);
        ExchangeRateApiResponse apiResponse = exchangeRateClient.fetchExchangeRates(baseCurrency);
        
        return ExchangeRatesResponse.builder()
                .base(apiResponse.getBase())
                .date(apiResponse.getDate())
                .rates(apiResponse.getRates())
                .build();
    }
    
    public ExchangeRatesResponse getExchangeRates() {
        return getExchangeRates(currencyConfig.getBase());
    }
}
