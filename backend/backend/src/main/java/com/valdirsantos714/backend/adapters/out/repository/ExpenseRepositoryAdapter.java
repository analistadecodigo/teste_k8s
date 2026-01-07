package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.ExpenseEntity;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.ExpenseMapper;
import com.valdirsantos714.backend.application.core.domain.Expense;
import com.valdirsantos714.backend.application.ports.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseJpaRepository expenseJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final String ENTITY_NOT_FOUND_MESSAGE = "Expense not found for email: ";
    private final String USER_NOT_FOUND_MESSAGE = "User not found for email: ";

    public ExpenseRepositoryAdapter(ExpenseJpaRepository expenseJpaRepository, UserJpaRepository userJpaRepository) {
        this.expenseJpaRepository = expenseJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Expense save(String email, Expense expense) {
        UserEntity userEntity = getUserByEmail(email);
        ExpenseEntity expenseEntity = ExpenseMapper.toExpenseEntity(expense);
        expenseEntity.setUser(userEntity);
        return ExpenseMapper.toExpense(expenseJpaRepository.save(expenseEntity));
    }

    @Override
    public List<Expense> findAll() {
        List<ExpenseEntity> entities = expenseJpaRepository.findAll();
        return entities.stream().map(ExpenseMapper::toExpense).toList();
    }

    @Override
    public Expense update(Long id, String email, Expense expense) {
        UserEntity userEntity = getUserByEmail(email);
        ExpenseEntity existingEntity = expenseJpaRepository.findByUserEmailAndId(email, id)
            .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + email));

        // Atualiza os campos mantendo o ID e usuário originais
        existingEntity.setName(expense.getName());
        existingEntity.setDescription(expense.getDescription());
        existingEntity.setAmount(expense.getAmount());
        existingEntity.setDate(expense.getDate());
        existingEntity.setCategory(expense.getCategory());
        existingEntity.setUser(userEntity);

        return ExpenseMapper.toExpense(expenseJpaRepository.save(existingEntity));
    }

    @Override
    public void delete(String email, Long id) {
        expenseJpaRepository.deleteByUserEmailAndId(email, id);
    }

    @Override
    public List<Expense> findByUserEmail(String email) {
        List<ExpenseEntity> entities = expenseJpaRepository.findByUserEmail(email);
        return entities.stream().map(ExpenseMapper::toExpense).toList();
    }

    private UserEntity getUserByEmail(String email) {
        return userJpaRepository.findByEmail(email)
            .map(UserEntity.class::cast)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + email));
    }
}
