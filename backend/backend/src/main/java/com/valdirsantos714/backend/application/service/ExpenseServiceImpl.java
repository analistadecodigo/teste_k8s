package com.valdirsantos714.backend.application.service;

import com.valdirsantos714.backend.adapters.in.dto.ExpenseRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.ExpenseRepositoryAdapter;
import com.valdirsantos714.backend.adapters.out.repository.mapper.ExpenseMapper;
import com.valdirsantos714.backend.application.core.domain.Expense;
import com.valdirsantos714.backend.application.usecase.ExpenseUseCases;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseUseCases {

    private final ExpenseRepositoryAdapter expenseRepositoryAdapter;

    public ExpenseServiceImpl(ExpenseRepositoryAdapter expenseRepositoryAdapter) {
        this.expenseRepositoryAdapter = expenseRepositoryAdapter;
    }

    @Override
    public Expense save(String email, ExpenseRequestDTO expense) {
        Expense expenseToSave = ExpenseMapper.toExpense(expense);
        return expenseRepositoryAdapter.save(email, expenseToSave);
    }

    @Override
    public List<Expense> findAll() {
        return expenseRepositoryAdapter.findAll();
    }

    @Override
    public Expense update(Long id, String email, ExpenseRequestDTO expense) {
        Expense expenseToUpdate = ExpenseMapper.toExpense(expense);
        return expenseRepositoryAdapter.update(id, email, expenseToUpdate);
    }

    @Override
    public void delete(String email, Long id) {
        expenseRepositoryAdapter.delete(email, id);
    }

    @Override
    public List<Expense> findByUserEmail(String email) {
        return expenseRepositoryAdapter.findByUserEmail(email);
    }
}