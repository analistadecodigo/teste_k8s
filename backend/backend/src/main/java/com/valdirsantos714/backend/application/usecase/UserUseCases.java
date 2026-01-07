package com.valdirsantos714.backend.application.usecase;

import com.valdirsantos714.backend.adapters.in.dto.UserRequestDTO;
import com.valdirsantos714.backend.application.core.domain.User;

import java.util.List;

public interface UserUseCases {
    User save(UserRequestDTO User);
    List<User> findAll();
    User findById(Long id);
    User findByEmail(String email);
    User update(Long id, UserRequestDTO User);
    void delete(Long id);
    String findNameByEmail(String email);
}
