package com.expensetracker.expense_tracker.ratelimit;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.expensetracker.expense_tracker.config.RateLimitConfig;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;

/**
 * Service for managing rate limiting using Bucket4j.
 * Maintains per-identifier (IP or user) buckets in memory.
 */
@Service
@RequiredArgsConstructor
public class RateLimitService {
    
    private final RateLimitConfig rateLimitConfig;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    public Bucket getAuthBucket(String identifier) {
        return buckets.computeIfAbsent("auth:" + identifier, key -> createBucket(
                rateLimitConfig.getAuthRequestsPerMinute(),
                rateLimitConfig.getTimeWindow()
        ));
    }
    
    public Bucket getExpenseCreationBucket(String identifier) {
        return buckets.computeIfAbsent("expense:" + identifier, key -> createBucket(
                rateLimitConfig.getExpenseCreationRequestsPerMinute(),
                rateLimitConfig.getTimeWindow()
        ));
    }
    
    public Bucket getGeneralBucket(String identifier) {
        return buckets.computeIfAbsent("general:" + identifier, key -> createBucket(
                rateLimitConfig.getGeneralRequestsPerMinute(),
                rateLimitConfig.getTimeWindow()
        ));
    }
    
    private Bucket createBucket(int capacity, Duration timeWindow) {
        Refill refill = Refill.intervally(capacity, timeWindow);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
    public boolean tryConsume(Bucket bucket) {
        return bucket.tryConsume(1);
    }
}
