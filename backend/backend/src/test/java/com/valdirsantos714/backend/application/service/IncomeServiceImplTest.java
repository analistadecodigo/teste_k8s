package com.valdirsantos714.backend.application.service;

import com.valdirsantos714.backend.adapters.in.dto.IncomeRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.IncomeRepositoryAdapter;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.core.domain.enums.IncomeCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {

    @Mock
    private IncomeRepositoryAdapter repository;

    @InjectMocks
    private IncomeServiceImpl service;

    private Income income;
    private IncomeRequestDTO incomeDTO;

    @BeforeEach
    void setup() {
        incomeDTO = new IncomeRequestDTO(
                "Salary",
                "Monthly salary",
                5000.0,
                LocalDate.of(2025, 1, 1),
                IncomeCategory.EARNINGS.toString()
        );

        income = new Income();
        income.setId(1L);
        income.setName(incomeDTO.name());
        income.setDescription(incomeDTO.description());
        income.setAmount(incomeDTO.amount());
        income.setDate(incomeDTO.date());
        income.setCategory(IncomeCategory.valueOf(incomeDTO.category()));
        income.setUser(new User());
    }

    @Test
    @DisplayName("should save a new income for a given user email")
    void shouldSaveIncome() {
        // given
        when(repository.save(anyString(), any(Income.class))).thenReturn(income);

        // when
        Income saved = service.save("admin@email.com", incomeDTO);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Salary");
        verify(repository, times(1)).save(anyString(), any());
    }

    @Test
    @DisplayName("should return all incomes")
    void shouldReturnAllIncomes() {
        // given
        when(repository.findAll()).thenReturn(List.of(income));

        // when
        List<Income> list = service.findAll();

        // then
        assertThat(list).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("should update an income by ID")
    void shouldUpdateIncome() {
        // given
        when(repository.update(eq(1L), eq("admin@email.com"), any())).thenReturn(income);

        // when
        Income updated = service.update(1L, "admin@email.com", incomeDTO);

        // then
        assertThat(updated.getName()).isEqualTo("Salary");
        verify(repository).update(eq(1L), eq("admin@email.com"), any());
    }

    @Test
    @DisplayName("should delete an income by ID")
    void shouldDeleteIncome() {
        // when
        service.delete("admin@email.com", 1L);

        // then
        verify(repository).delete("admin@email.com", 1L);
    }

    @Test
    @DisplayName("should find incomes by user email")
    void shouldFindByUserEmail() {
        // given
        when(repository.findByUserEmail("admin@email.com")).thenReturn(Collections.singletonList(income));

        // when
        List<Income> result = service.findByUserEmail("admin@email.com");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Salary");
        verify(repository).findByUserEmail("admin@email.com");
    }
}