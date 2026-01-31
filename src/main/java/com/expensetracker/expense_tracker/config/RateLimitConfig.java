package com.expensetracker.expense_tracker.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Rate limiting configuration properties.
 * Externalized to application.yaml for environment-specific tuning.
 */
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Getter
@Setter
public class RateLimitConfig {
    
    private int authRequestsPerMinute = 5;
    private int expenseCreationRequestsPerMinute = 10;
    private int generalRequestsPerMinute = 30;
    private int timeWindowMinutes = 1;
    
    public Duration getTimeWindow() {
        return Duration.ofMinutes(timeWindowMinutes);
    }
}
