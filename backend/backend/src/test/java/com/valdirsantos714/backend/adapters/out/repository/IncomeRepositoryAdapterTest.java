package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.IncomeEntity;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.IncomeMapper;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.core.domain.enums.IncomeCategory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeRepositoryAdapterTest {

    @Mock
    private IncomeJpaRepository incomeJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private IncomeRepositoryAdapter incomeRepositoryAdapter;

    private final String TEST_EMAIL = "test@example.com";
    private final Long TEST_ID = 1L;

    @Test
    @DisplayName("should save income successfully")
    void shouldSaveIncomeSuccessfully() {
        // Given
        Income income = new Income();
        income.setName("Test Income");
        income.setDescription("Description");
        income.setAmount(100.0);
        income.setDate(LocalDate.now());
        income.setCategory(IncomeCategory.SALES);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);

        IncomeEntity incomeEntity = IncomeMapper.toIncomeEntity(income);
        incomeEntity.setUser(userEntity);
        incomeEntity.setId(TEST_ID);

        when(userJpaRepository.findUserEntityByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));
        when(incomeJpaRepository.save(any(IncomeEntity.class))).thenReturn(incomeEntity);

        // When
        Income savedIncome = incomeRepositoryAdapter.save(TEST_EMAIL, income);

        // Then
        assertThat(savedIncome).isNotNull();
        assertThat(savedIncome.getName()).isEqualTo(income.getName());
        assertThat(savedIncome.getId()).isEqualTo(TEST_ID);
        verify(userJpaRepository, times(1)).findUserEntityByEmail(TEST_EMAIL);
        verify(incomeJpaRepository, times(1)).save(any(IncomeEntity.class));
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when saving income with non-existent user email")
    void shouldThrowExceptionWhenSavingIncomeWithNonExistentUserEmail() {
        // Given
        Income income = new Income();
        when(userJpaRepository.findUserEntityByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> incomeRepositoryAdapter.save(TEST_EMAIL, income));
        verify(userJpaRepository, times(1)).findUserEntityByEmail(TEST_EMAIL);
        verify(incomeJpaRepository, never()).save(any(IncomeEntity.class));
    }

    @Test
    @DisplayName("should find all incomes successfully")
    void shouldFindAllIncomesSuccessfully() {
        // Given
        IncomeEntity incomeEntity = new IncomeEntity();
        incomeEntity.setId(TEST_ID);
        incomeEntity.setName("Income 1");
        List<IncomeEntity> incomeEntities = Collections.singletonList(incomeEntity);

        when(incomeJpaRepository.findAll()).thenReturn(incomeEntities);

        // When
        List<Income> incomes = incomeRepositoryAdapter.findAll();

        // Then
        assertThat(incomes).hasSize(1);
        assertThat(incomes.get(0).getName()).isEqualTo("Income 1");
        verify(incomeJpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should update income successfully")
    void shouldUpdateIncomeSuccessfully() {
        // Given
        Income income = new Income();
        income.setName("Updated Income");
        income.setDescription("Updated Description");
        income.setAmount(200.0);
        income.setDate(LocalDate.now());
        income.setCategory(IncomeCategory.COMMISSION);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);

        IncomeEntity existingIncomeEntity = new IncomeEntity();
        existingIncomeEntity.setId(TEST_ID);
        existingIncomeEntity.setName("Old Income");
        existingIncomeEntity.setUser(userEntity);

        IncomeEntity updatedIncomeEntity = IncomeMapper.toIncomeEntity(income);
        updatedIncomeEntity.setId(TEST_ID);
        updatedIncomeEntity.setUser(userEntity);

        when(userJpaRepository.findUserEntityByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));
        when(incomeJpaRepository.findByUserEmailAndId(TEST_EMAIL, TEST_ID)).thenReturn(Optional.of(existingIncomeEntity));
        when(incomeJpaRepository.save(any(IncomeEntity.class))).thenReturn(updatedIncomeEntity);

        // When
        Income result = incomeRepositoryAdapter.update(TEST_ID, TEST_EMAIL, income);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(income.getName());
        assertThat(result.getAmount()).isEqualTo(income.getAmount());
        verify(userJpaRepository, times(1)).findUserEntityByEmail(TEST_EMAIL);
        verify(incomeJpaRepository, times(1)).findByUserEmailAndId(TEST_EMAIL, TEST_ID);
        verify(incomeJpaRepository, times(1)).save(existingIncomeEntity);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when updating non-existent income")
    void shouldThrowExceptionWhenUpdatingNonExistentIncome() {
        // Given
        Income income = new Income();
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);

        when(userJpaRepository.findUserEntityByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));
        when(incomeJpaRepository.findByUserEmailAndId(TEST_EMAIL, TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> incomeRepositoryAdapter.update(TEST_ID, TEST_EMAIL, income));
        verify(userJpaRepository, times(1)).findUserEntityByEmail(TEST_EMAIL);
        verify(incomeJpaRepository, times(1)).findByUserEmailAndId(TEST_EMAIL, TEST_ID);
        verify(incomeJpaRepository, never()).save(any(IncomeEntity.class));
    }

    @Test
    @DisplayName("should delete income successfully")
    void shouldDeleteIncomeSuccessfully() {
        // Given
        when(incomeJpaRepository.findByUserEmailAndId(TEST_EMAIL, TEST_ID)).thenReturn(Optional.of(new IncomeEntity()));
        doNothing().when(incomeJpaRepository).deleteByUserEmailAndId(TEST_EMAIL, TEST_ID);

        // When
        incomeRepositoryAdapter.delete(TEST_EMAIL, TEST_ID);

        // Then
        verify(incomeJpaRepository, times(1)).findByUserEmailAndId(TEST_EMAIL, TEST_ID);
        verify(incomeJpaRepository, times(1)).deleteByUserEmailAndId(TEST_EMAIL, TEST_ID);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when deleting non-existent income")
    void shouldThrowExceptionWhenDeletingNonExistentIncome() {
        // Given
        when(incomeJpaRepository.findByUserEmailAndId(TEST_EMAIL, TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> incomeRepositoryAdapter.delete(TEST_EMAIL, TEST_ID));
        verify(incomeJpaRepository, times(1)).findByUserEmailAndId(TEST_EMAIL, TEST_ID);
        verify(incomeJpaRepository, never()).deleteByUserEmailAndId(anyString(), anyLong());
    }

    @Test
    @DisplayName("should find incomes by user email successfully")
    void shouldFindIncomesByUserEmailSuccessfully() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);
        IncomeEntity incomeEntity = new IncomeEntity();
        incomeEntity.setId(TEST_ID);
        incomeEntity.setName("User Income");
        List<IncomeEntity> incomeEntities = Collections.singletonList(incomeEntity);

        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));
        when(incomeJpaRepository.findByUserEmail(TEST_EMAIL)).thenReturn(incomeEntities);

        // When
        List<Income> incomes = incomeRepositoryAdapter.findByUserEmail(TEST_EMAIL);

        // Then
        assertThat(incomes).hasSize(1);
        assertThat(incomes.get(0).getName()).isEqualTo("User Income");
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(incomeJpaRepository, times(1)).findByUserEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when finding incomes by non-existent user email")
    void shouldThrowExceptionWhenFindingIncomesByNonExistentUserEmail() {
        // Given
        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> incomeRepositoryAdapter.findByUserEmail(TEST_EMAIL));
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(incomeJpaRepository, never()).findByUserEmail(anyString());
    }
}
