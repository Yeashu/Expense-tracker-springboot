package com.expensetracker.expense_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.service.ExpenseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "APIs for managing user expenses")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {
    private final ExpenseService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new expense",
        description = "Adds a new expense for the authenticated user. Requires JWT authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Expense successfully created",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or validation error",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token missing or invalid",
            content = @Content
        )
    })
    public ExpenseResponse addExpense(
        @RequestBody @Valid ExpenseRequest req
    ){
        return service.addExpense(req);
    }

    @GetMapping
    @Operation(
        summary = "Get all expenses",
        description = "Retrieves all expenses for the authenticated user. Requires JWT authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved expenses",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token missing or invalid",
            content = @Content
        )
    })
    public List<ExpenseResponse> getExpense(){
        return service.getExpenses();
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an expense",
        description = "Updates an existing expense by ID. Only the owner of the expense can update it. Requires JWT authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Expense successfully updated",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data, expense not found, or unauthorized access",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token missing or invalid",
            content = @Content
        )
    })
    public ExpenseResponse updateExpense(
        @PathVariable Long id,
        @RequestBody @Valid ExpenseRequest req
    ){
        return service.updateExpense(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete an expense",
        description = "Deletes an expense by ID. Only the owner of the expense can delete it. Requires JWT authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Expense successfully deleted"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Expense not found or unauthorized access",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token missing or invalid",
            content = @Content
        )
    })
    public void deleteExpense(@PathVariable Long id){
        service.deleteExpense(id);
    }
}
