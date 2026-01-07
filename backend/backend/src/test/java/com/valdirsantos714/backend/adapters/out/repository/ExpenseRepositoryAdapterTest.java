package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.ExpenseEntity;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.ExpenseMapper;
import com.valdirsantos714.backend.application.core.domain.Expense;
import com.valdirsantos714.backend.application.core.domain.enums.ExpenseCategory;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseRepositoryAdapterTest {

    @Mock
    private ExpenseJpaRepository expenseJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private ExpenseRepositoryAdapter expenseRepositoryAdapter;

    private final String TEST_EMAIL = "test@example.com";
    private final Long TEST_ID = 1L;

    @Test
    @DisplayName("should save expense successfully")
    void shouldSaveExpenseSuccessfully() {
        // Given
        Expense expense = new Expense();
        expense.setName("Test Expense");
        expense.setDescription("Description");
        expense.setAmount(100.0);
        expense.setDate(LocalDate.now());
        expense.setCategory(ExpenseCategory.FOOD);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);

        ExpenseEntity expenseEntity = ExpenseMapper.toExpenseEntity(expense);
        expenseEntity.setUser(userEntity);
        expenseEntity.setId(TEST_ID);

        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));
        when(expenseJpaRepository.save(any(ExpenseEntity.class))).thenReturn(expenseEntity);

        // When
        Expense savedExpense = expenseRepositoryAdapter.save(TEST_EMAIL, expense);

        // Then
        assertThat(savedExpense).isNotNull();
        assertThat(savedExpense.getName()).isEqualTo(expense.getName());
        assertThat(savedExpense.getId()).isEqualTo(TEST_ID);
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(expenseJpaRepository, times(1)).save(any(ExpenseEntity.class));
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when saving expense with non-existent user email")
    void shouldThrowExceptionWhenSavingExpenseWithNonExistentUserEmail() {
        // Given
        Expense expense = new Expense();
        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> expenseRepositoryAdapter.save(TEST_EMAIL, expense));
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(expenseJpaRepository, never()).save(any(ExpenseEntity.class));
    }

    @Test
    @DisplayName("should find all expenses successfully")
    void shouldFindAllExpensesSuccessfully() {
        // Given
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setId(TEST_ID);
        expenseEntity.setName("Expense 1");
        List<ExpenseEntity> expenseEntities = Collections.singletonList(expenseEntity);

        when(expenseJpaRepository.findAll()).thenReturn(expenseEntities);

        // When
        List<Expense> expenses = expenseRepositoryAdapter.findAll();

        // Then
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getName()).isEqualTo("Expense 1");
        verify(expenseJpaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should update expense successfully")
    void shouldUpdateExpenseSuccessfully() {
        // Given
        Expense expense = new Expense();
        expense.setName("Updated Expense");
        expense.setDescription("Updated Description");
        expense.setAmount(200.0);
        expense.setDate(LocalDate.now());
        expense.setCategory(ExpenseCategory.LEISURE);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);

        ExpenseEntity existingExpenseEntity = new ExpenseEntity();
        existingExpenseEntity.setId(TEST_ID);
        existingExpenseEntity.setName("Old Expense");
        existingExpenseEntity.setUser(userEntity);

        ExpenseEntity updatedExpenseEntity = ExpenseMapper.toExpenseEntity(expense);
        updatedExpenseEntity.setId(TEST_ID);
        updatedExpenseEntity.setUser(userEntity);

        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));
        when(expenseJpaRepository.findByUserEmailAndId(TEST_EMAIL, TEST_ID)).thenReturn(Optional.of(existingExpenseEntity));
        when(expenseJpaRepository.save(any(ExpenseEntity.class))).thenReturn(updatedExpenseEntity);

        // When
        Expense result = expenseRepositoryAdapter.update(TEST_ID, TEST_EMAIL, expense);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(expense.getName());
        assertThat(result.getAmount()).isEqualTo(expense.getAmount());
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(expenseJpaRepository, times(1)).findByUserEmailAndId(TEST_EMAIL, TEST_ID);
        verify(expenseJpaRepository, times(1)).save(existingExpenseEntity);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when updating non-existent expense")
    void shouldThrowExceptionWhenUpdatingNonExistentExpense() {
        // Given
        Expense expense = new Expense();
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(TEST_EMAIL);

        when(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userEntity));
        when(expenseJpaRepository.findByUserEmailAndId(TEST_EMAIL, TEST_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> expenseRepositoryAdapter.update(TEST_ID, TEST_EMAIL, expense));
        verify(userJpaRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(expenseJpaRepository, times(1)).findByUserEmailAndId(TEST_EMAIL, TEST_ID);
        verify(expenseJpaRepository, never()).save(any(ExpenseEntity.class));
    }

    @Test
    @DisplayName("should delete expense successfully")
    void shouldDeleteExpenseSuccessfully() {
        // Given
        doNothing().when(expenseJpaRepository).deleteByUserEmailAndId(TEST_EMAIL, TEST_ID);

        // When
        expenseRepositoryAdapter.delete(TEST_EMAIL, TEST_ID);

        // Then
        verify(expenseJpaRepository, times(1)).deleteByUserEmailAndId(TEST_EMAIL, TEST_ID);
    }

    @Test
    @DisplayName("should find expenses by user email successfully")
    void shouldFindExpensesByUserEmailSuccessfully() {
        // Given
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setId(TEST_ID);
        expenseEntity.setName("User Expense");
        List<ExpenseEntity> expenseEntities = Collections.singletonList(expenseEntity);

        when(expenseJpaRepository.findByUserEmail(TEST_EMAIL)).thenReturn(expenseEntities);

        // When
        List<Expense> expenses = expenseRepositoryAdapter.findByUserEmail(TEST_EMAIL);

        // Then
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getName()).isEqualTo("User Expense");
        verify(expenseJpaRepository, times(1)).findByUserEmail(TEST_EMAIL);
    }
}
