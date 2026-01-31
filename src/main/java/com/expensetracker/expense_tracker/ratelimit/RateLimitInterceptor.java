package com.expensetracker.expense_tracker.ratelimit;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.expensetracker.expense_tracker.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor for rate limiting API requests.
 * Uses IP address or authenticated user email as identifier.
 */
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String identifier = getIdentifier(request);
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        Bucket bucket = getBucketForPath(path, method, identifier);
        
        if (!rateLimitService.tryConsume(bucket)) {
            handleRateLimitExceeded(response, path);
            return false;
        }
        
        return true;
    }
    
    private String getIdentifier(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getName())) {
            return "user:" + authentication.getName();
        }
        
        return "ip:" + getClientIpAddress(request);
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
    
    private Bucket getBucketForPath(String path, String method, String identifier) {
        if (path.startsWith("/auth/")) {
            return rateLimitService.getAuthBucket(identifier);
        }
        if (path.equals("/expenses") && "POST".equals(method)) {
            return rateLimitService.getExpenseCreationBucket(identifier);
        }
        return rateLimitService.getGeneralBucket(identifier);
    }
    
    private void handleRateLimitExceeded(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("Too Many Requests")
                .message("Rate limit exceeded. Please try again later.")
                .path(path)
                .build();
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
