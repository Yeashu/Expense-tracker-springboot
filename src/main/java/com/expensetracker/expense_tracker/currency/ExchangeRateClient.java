package com.expensetracker.expense_tracker.currency;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.expensetracker.expense_tracker.config.CurrencyConfig;
import com.expensetracker.expense_tracker.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Client for fetching exchange rates from external API.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateClient {
    
    private final RestTemplate restTemplate;
    private final CurrencyConfig currencyConfig;
    
    /**
     * Fetches latest exchange rates for the base currency.
     * @param baseCurrency Base currency code (e.g., "INR")
     * @return ExchangeRateApiResponse with rates
     * @throws ResourceNotFoundException if API call fails
     */
    public ExchangeRateApiResponse fetchExchangeRates(String baseCurrency) {
        String apiUrl = buildApiUrl(baseCurrency);
        
        try {
            log.debug("Fetching exchange rates from: {}", apiUrl);
            ResponseEntity<ExchangeRateApiResponse> response = restTemplate.getForEntity(
                    apiUrl, 
                    ExchangeRateApiResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.debug("Successfully fetched exchange rates for base: {}", baseCurrency);
                return response.getBody();
            }
            
            throw new ResourceNotFoundException("Failed to fetch exchange rates from API");
            
        } catch (RestClientException e) {
            log.error("Error fetching exchange rates from API: {}", e.getMessage());
            throw new ResourceNotFoundException(
                    "Currency API is currently unavailable. Please try again later."
            );
        }
    }
    
    private String buildApiUrl(String baseCurrency) {
        String baseUrl = currencyConfig.getApi().getUrl();
        String apiKey = currencyConfig.getApi().getKey();
        
        if (baseUrl.contains("exchangerate-api.com")) {
            return String.format("%s/latest/%s", baseUrl, baseCurrency);
        } else if (baseUrl.contains("exchangerate.host")) {
            if (apiKey != null && !apiKey.isBlank()) {
                return String.format("%s/latest?base=%s&access_key=%s", baseUrl, baseCurrency, apiKey);
            }
            return String.format("%s/latest?base=%s", baseUrl, baseCurrency);
        } else {
            if (apiKey != null && !apiKey.isBlank()) {
                return String.format("%s?base=%s&access_key=%s", baseUrl, baseCurrency, apiKey);
            }
            return String.format("%s?base=%s", baseUrl, baseCurrency);
        }
    }
}
