package com.valdirsantos714.backend.application.service;

import com.valdirsantos714.backend.adapters.in.dto.ExpenseRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.ExpenseRepositoryAdapter;
import com.valdirsantos714.backend.application.core.domain.Expense;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.core.domain.enums.ExpenseCategory;
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
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepositoryAdapter expenseRepositoryAdapter;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    @DisplayName("should save expense when given valid email and expense dto")
    void shouldSaveExpenseWhenGivenValidEmailAndExpenseDto() {
        // given
        ExpenseRequestDTO requestDTO = new ExpenseRequestDTO(
                "Viagem",
                "Viagem para o RJ",
                500.00,
                LocalDate.now(),
                ExpenseCategory.LEISURE
        );

        Expense expected = new Expense();
        expected.setId(null);
        expected.setName("Viagem");
        expected.setDescription("Viagem para o RJ");
        expected.setAmount(500.00);
        expected.setDate(LocalDate.now());
        expected.setCategory(ExpenseCategory.LEISURE);
        expected.setUser(new User());

        when(expenseRepositoryAdapter.save(anyString(), any(Expense.class))).thenReturn(expected);

        // when
        Expense result = expenseService.save("admin@email.com", requestDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Viagem");
        verify(expenseRepositoryAdapter, times(1)).save(eq("admin@email.com"), any(Expense.class));
    }

    @Test
    @DisplayName("should return expense list when findAll is called")
    void shouldReturnExpenseListWhenFindAllIsCalled() {
        // given
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setName("Compra");
        expense.setDescription("Supermercado");
        expense.setAmount(200.00);
        expense.setDate(LocalDate.now());
        expense.setCategory(ExpenseCategory.FOOD);

        List<Expense> expectedList = Collections.singletonList(expense);
        when(expenseRepositoryAdapter.findAll()).thenReturn(expectedList);

        // when
        List<Expense> result = expenseService.findAll();

        // then
        assertThat(result).hasSize(1);
        verify(expenseRepositoryAdapter, times(1)).findAll();
    }

    @Test
    @DisplayName("should update and return expense when given id, email and expense dto")
    void shouldUpdateAndReturnExpenseWhenGivenIdEmailAndExpenseDto() {
        // given
        ExpenseRequestDTO dto = new ExpenseRequestDTO(
                "Editado",
                "Editado",
                300.00,
                LocalDate.now(),
                ExpenseCategory.FOOD
        );

        Expense expected = new Expense();
        expected.setId(1L);
        expected.setName("Editado");
        expected.setDescription("Editado");
        expected.setAmount(300.00);
        expected.setDate(LocalDate.now());
        expected.setCategory(ExpenseCategory.FOOD);
        expected.setUser(new User());

        when(expenseRepositoryAdapter.update(eq(1L), anyString(), any(Expense.class))).thenReturn(expected);

        // when
        Expense result = expenseService.update(1L, "admin@email.com", dto);

        // then
        assertThat(result.getName()).isEqualTo("Editado");
        verify(expenseRepositoryAdapter).update(eq(1L), eq("admin@email.com"), any(Expense.class));
    }

    @Test
    @DisplayName("should call delete on repository when given email and id")
    void shouldCallDeleteOnRepositoryWhenGivenEmailAndId() {
        // given
        doNothing().when(expenseRepositoryAdapter).delete("admin@email.com", 1L);

        // when
        expenseService.delete("admin@email.com", 1L);

        // then
        verify(expenseRepositoryAdapter).delete("admin@email.com", 1L);
    }

    @Test
    @DisplayName("should return expense list when findByUserEmail is called")
    void shouldReturnExpenseListWhenFindByUserEmailIsCalled() {
        // given
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setName("Luz");
        expense.setDescription("Conta de luz");
        expense.setAmount(150.00);
        expense.setDate(LocalDate.now());
        expense.setCategory(ExpenseCategory.SERVICES);
        expense.setUser(new User());

        List<Expense> expected = Collections.singletonList(expense);
        when(expenseRepositoryAdapter.findByUserEmail("admin@email.com")).thenReturn(expected);

        // when
        List<Expense> result = expenseService.findByUserEmail("admin@email.com");

        // then
        assertThat(result).hasSize(1);
        verify(expenseRepositoryAdapter).findByUserEmail("admin@email.com");
    }
}