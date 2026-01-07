package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.ExpenseMapper;
import com.valdirsantos714.backend.adapters.out.repository.mapper.IncomeMapper;
import com.valdirsantos714.backend.adapters.out.repository.mapper.UserMapper;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.ports.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final String ENTITY_NOT_FOUND_MESSAGE = "User not found with id: ";

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User User) {
        UserEntity entity = new UserEntity(User);
        userJpaRepository.save(entity);
        return UserMapper.toUser(entity);
    }

    @Override
    public List<User> findAll() {
        List<UserEntity> entities = userJpaRepository.findAll();
        return entities.stream().map(UserMapper::toUser).toList();
    }

    @Override
    public User findById(Long id) {
        return userJpaRepository.findById(id)
                .map(UserMapper::toUser)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + id));
    }

    @Override
    public User update(Long id, User User) {
        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND_MESSAGE + id));

        entity = updateUserEntity(entity, User);
        userJpaRepository.save(entity);
        return UserMapper.toUser(entity);
    }

    @Override
    public void delete(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public UserDetails findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public String findNameByEmail(String email) {
        return userJpaRepository.findNameByEmail(email);
    }

    private UserEntity updateUserEntity(UserEntity entity, User user) {
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setIncomes(IncomeMapper.toIncomeEntityList(user.getIncomes()));
        entity.setExpenses(ExpenseMapper.toExpenseEntityList(user.getExpenses()));
        return entity;
    }
}
