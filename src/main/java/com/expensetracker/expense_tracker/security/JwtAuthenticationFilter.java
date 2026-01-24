package com.expensetracker.expense_tracker.security;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    
    private final JwtUtil jwtUtil;
    private final UserAuthenticationService authService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain filterChain
    ) throws java.io.IOException, ServletException {
        String authHeader = req.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            if(email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null
            ){
                var userDetails = authService.loadUserByUsername(email);
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());

                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(req)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(req, res);
    }
}
