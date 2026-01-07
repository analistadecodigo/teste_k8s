package com.valdirsantos714.backend.application.service;

import com.valdirsantos714.backend.adapters.in.dto.IncomeRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.IncomeRepositoryAdapter;
import com.valdirsantos714.backend.adapters.out.repository.mapper.IncomeMapper;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.usecase.IncomeUseCases;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeServiceImpl implements IncomeUseCases {

    private final IncomeRepositoryAdapter incomeRepositoryAdapter;

    public IncomeServiceImpl(IncomeRepositoryAdapter incomeRepositoryAdapter) {
        this.incomeRepositoryAdapter = incomeRepositoryAdapter;
    }

    @Override
    public Income save(String email, IncomeRequestDTO income) {
        Income incomeToSave = IncomeMapper.toIncome(income);
        return incomeRepositoryAdapter.save(email, incomeToSave);
    }

    @Override
    public List<Income> findAll() {
        return incomeRepositoryAdapter.findAll();
    }

    public Income update(Long id, String email, IncomeRequestDTO income) {
        Income incomeToUpdate = IncomeMapper.toIncome(income);
        return incomeRepositoryAdapter.update(id, email, incomeToUpdate);
    }

    @Override
    public void delete(String email, Long id) {
        incomeRepositoryAdapter.delete(email, id);
    }

    @Override
    public List<Income> findByUserEmail(String email) {
        return incomeRepositoryAdapter.findByUserEmail(email);
    }
}
