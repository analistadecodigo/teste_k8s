package com.valdirsantos714.backend.application.service;

import com.valdirsantos714.backend.adapters.out.dto.FinancialSummaryResponseDTO;
import com.valdirsantos714.backend.adapters.out.repository.ExpenseJpaRepository;
import com.valdirsantos714.backend.adapters.out.repository.IncomeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialSummaryServiceImplTest {

    @Mock
    private ExpenseJpaRepository expenseRepository;

    @Mock
    private IncomeJpaRepository incomeRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FinancialSummaryServiceImpl financialSummaryService;

    @Test
    @DisplayName("should return financial summary for a valid user email")
    void shouldReturnFinancialSummaryForValidEmail() {
        // Given
        String email = "usuario@email.com";
        String expectedName = "João";
        Double expectedIncome = 5000.0;
        Double expectedExpense = 1500.0;

        when(userService.findNameByEmail(email)).thenReturn(expectedName);
        when(incomeRepository.sumIncomesByUserEmail(email)).thenReturn(expectedIncome);
        when(expenseRepository.sumExpensesByUserEmail(email)).thenReturn(expectedExpense);

        // When
        FinancialSummaryResponseDTO summary = financialSummaryService.getFinancialSummary(email);

        // Then
        assertNotNull(summary);
        assertEquals(expectedName, summary.name());
        assertEquals(expectedIncome, summary.totalIncome());
        assertEquals(expectedExpense, summary.totalExpenses());
        assertEquals(expectedIncome - expectedExpense, summary.balance());
    }
}