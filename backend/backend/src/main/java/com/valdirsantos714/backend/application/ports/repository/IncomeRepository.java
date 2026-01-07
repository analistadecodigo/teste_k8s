package com.valdirsantos714.backend.application.ports.repository;

import com.valdirsantos714.backend.application.core.domain.Income;

import java.util.List;

public interface IncomeRepository {
    Income save(String email, Income income);
    List<Income> findAll();
    Income update(Long id, String email, Income income);
    void delete(String email, Long id);
    List<Income> findByUserEmail(String email);
}
