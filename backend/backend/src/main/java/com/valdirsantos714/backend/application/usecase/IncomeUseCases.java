package com.valdirsantos714.backend.application.usecase;

import com.valdirsantos714.backend.adapters.in.dto.IncomeRequestDTO;
import com.valdirsantos714.backend.application.core.domain.Income;

import java.util.List;

public interface IncomeUseCases {
    Income save(String email, IncomeRequestDTO income);
    List<Income> findAll();
    Income update(Long id, String email, IncomeRequestDTO income);
    void delete(String email, Long id);
    List<Income> findByUserEmail(String email);
}
