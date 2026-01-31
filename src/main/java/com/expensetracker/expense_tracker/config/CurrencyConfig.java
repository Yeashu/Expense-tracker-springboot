package com.expensetracker.expense_tracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "currency")
@Getter
@Setter
public class CurrencyConfig {
    
    private Api api = new Api();
    private String base = "INR";
    
    @Getter
    @Setter
    public static class Api {
        private String url;
        private String key;
    }
}
