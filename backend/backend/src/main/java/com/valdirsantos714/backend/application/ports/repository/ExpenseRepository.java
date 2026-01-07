package com.valdirsantos714.backend.application.ports.repository;

import com.valdirsantos714.backend.application.core.domain.Expense;

import java.util.List;

public interface ExpenseRepository {
    Expense save(String email, Expense expense);
    List<Expense> findAll();
    Expense update(Long id, String email, Expense expense);
    void delete(String email, Long id);
    List<Expense> findByUserEmail(String email);
}
