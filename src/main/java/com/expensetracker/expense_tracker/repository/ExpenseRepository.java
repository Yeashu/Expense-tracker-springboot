package com.expensetracker.expense_tracker.repository;

import org.springframework.stereotype.Repository;

import com.expensetracker.expense_tracker.model.Expense;
import com.expensetracker.expense_tracker.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>{
    List<Expense> findByUser(User user);
}
