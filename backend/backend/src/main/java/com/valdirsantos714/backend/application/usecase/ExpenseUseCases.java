package com.valdirsantos714.backend.application.usecase;

import com.valdirsantos714.backend.adapters.in.dto.ExpenseRequestDTO;
import com.valdirsantos714.backend.application.core.domain.Expense;

import java.util.List;

public interface ExpenseUseCases {
    Expense save(String email, ExpenseRequestDTO expense);
    List<Expense> findAll();
    Expense update(Long id, String email, ExpenseRequestDTO expense);
    void delete(String email, Long id);
    List<Expense> findByUserEmail(String email);
}
