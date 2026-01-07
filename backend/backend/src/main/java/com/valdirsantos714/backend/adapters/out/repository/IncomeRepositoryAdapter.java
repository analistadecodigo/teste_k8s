package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.IncomeEntity;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.IncomeMapper;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.ports.repository.IncomeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IncomeRepositoryAdapter implements IncomeRepository {

    private final IncomeJpaRepository incomeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final String ENTITY_NOT_FOUND_MESSAGE = "Income not found for email: ";
    private final String USER_NOT_FOUND_MESSAGE = "User not found for email: ";

    public IncomeRepositoryAdapter(IncomeJpaRepository incomeJpaRepository, UserJpaRepository userJpaRepository) {
        this.incomeJpaRepository = incomeJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Income save(String email, Income income) {
        UserEntity userEntity = userJpaRepository.findUserEntityByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + email));

        IncomeEntity entity = IncomeMapper.toIncomeEntity(income);
        entity.setUser(userEntity);
        return IncomeMapper.toIncome(incomeJpaRepository.save(entity));
    }

    @Override
    public List<Income> findAll() {
        List<IncomeEntity> entities = incomeJpaRepository.findAll();
        return entities.stream().map(IncomeMapper::toIncome).toList();
    }

    public Income update(Long id, String email, Income income) {
        UserEntity userEntity = userJpaRepository.findUserEntityByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + email));

        IncomeEntity existingEntity = incomeJpaRepository.findByUserEmailAndId(email, id)
            .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + email));

        // Atualiza os campos mantendo o ID e usuário originais
        existingEntity.setName(income.getName());
        existingEntity.setDescription(income.getDescription());
        existingEntity.setAmount(income.getAmount());
        existingEntity.setDate(income.getDate());
        existingEntity.setCategory(income.getCategory());
        existingEntity.setUser(userEntity);

        return IncomeMapper.toIncome(incomeJpaRepository.save(existingEntity));
    }

    @Override
    public void delete(String email, Long id) {
        if (!incomeJpaRepository.findByUserEmailAndId(email, id).isEmpty()) {
            incomeJpaRepository.deleteByUserEmailAndId(email, id);
        } else {
            throw new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + email);
        }
    }

    @Override
    public List<Income> findByUserEmail(String email) {
        if (!userJpaRepository.findByEmail(email).isPresent()) {
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + email);
        }
        List<IncomeEntity> entities = incomeJpaRepository.findByUserEmail(email);
        return entities.stream().map(IncomeMapper::toIncome).toList();
    }
}