package com.expensetracker.expense_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expense_tracker.dto.CurrencyConvertResponse;
import com.expensetracker.expense_tracker.dto.ExchangeRatesResponse;
import com.expensetracker.expense_tracker.service.CurrencyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
@Tag(name = "Currency", description = "APIs for currency conversion and exchange rates")
public class CurrencyController {
    
    private final CurrencyService currencyService;
    
    @GetMapping("/convert")
    @Operation(
        summary = "Convert currency amount",
        description = "Converts an amount from one currency to another using live exchange rates. " +
                      "Exchange rates are cached for 1 hour to optimize performance."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Currency conversion successful",
            content = @Content(schema = @Schema(implementation = CurrencyConvertResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input (amount, currency codes)",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Currency API unavailable or currency not found",
            content = @Content
        )
    })
    public CurrencyConvertResponse convertCurrency(
            @RequestParam Double amount,
            @RequestParam String from,
            @RequestParam String to) {
        return currencyService.convertCurrency(amount, from, to);
    }
    
    @GetMapping("/rates")
    @Operation(
        summary = "Get exchange rates",
        description = "Retrieves all available exchange rates for the base currency (default: INR). " +
                      "Results are cached for 1 hour."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Exchange rates retrieved successfully",
            content = @Content(schema = @Schema(implementation = ExchangeRatesResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Currency API unavailable",
            content = @Content
        )
    })
    public ExchangeRatesResponse getExchangeRates(
            @RequestParam(required = false) String base) {
        if (base != null && !base.isBlank()) {
            return currencyService.getExchangeRates(base);
        }
        return currencyService.getExchangeRates();
    }
}
