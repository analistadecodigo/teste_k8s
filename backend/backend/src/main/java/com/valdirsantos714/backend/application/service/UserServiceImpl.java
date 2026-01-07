package com.valdirsantos714.backend.application.service;

import com.valdirsantos714.backend.adapters.in.dto.UserRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.UserRepositoryAdapter;
import com.valdirsantos714.backend.adapters.out.repository.mapper.UserMapper;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.usecase.UserUseCases;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserUseCases {

    private final UserRepositoryAdapter userRepositoryAdapter;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepositoryAdapter userRepositoryAdapter, PasswordEncoder passwordEncoder) {
        this.userRepositoryAdapter = userRepositoryAdapter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(UserRequestDTO user) {
        String encodedPassword = passwordEncoder.encode(user.password());
        User userToSave = UserMapper.toUser(user);
        userToSave.setPassword(encodedPassword);
        return userRepositoryAdapter.save(userToSave);
    }

    @Override
    public List<User> findAll() {
        return userRepositoryAdapter.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepositoryAdapter.findById(id);
    }

    @Override
    public User findByEmail(String email) {
        return UserMapper.toUser(userRepositoryAdapter.findByEmail(email));
    }

    @Override
    public User update(Long id, UserRequestDTO user) {
        return userRepositoryAdapter.update(id, UserMapper.toUser(user));
    }

    @Override
    public void delete(Long id) {
        userRepositoryAdapter.delete(id);
    }

    @Override
    public String findNameByEmail(String email) {
        return userRepositoryAdapter.findNameByEmail(email);
    }
}