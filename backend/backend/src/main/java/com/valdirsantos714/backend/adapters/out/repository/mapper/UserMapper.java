package com.valdirsantos714.backend.adapters.out.repository.mapper;

import com.valdirsantos714.backend.adapters.in.dto.UserRequestDTO;
import com.valdirsantos714.backend.adapters.out.dto.UserResponseDTO;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.application.core.domain.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static User toUser(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setName(userEntity.getName());
        user.setEmail(userEntity.getEmail());
        user.setPassword(userEntity.getPassword());
        user.setIncomes(IncomeMapper.toIncomeList(userEntity.getIncomes()));
        user.setExpenses(ExpenseMapper.toExpenseList(userEntity.getExpenses()));
        return user;
    }

    public static User toUser(UserDetails userDetails) {
        User user = new User();
        user.setEmail(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        return user;
    }

    public static UserEntity toUserEntity(User user) {
        return new UserEntity(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole() != null ? user.getRole() : null,
            user.getIncomes() != null ? IncomeMapper.toIncomeEntityList(user.getIncomes()) : new ArrayList<>(),
            user.getExpenses() != null ? ExpenseMapper.toExpenseEntityList(user.getExpenses()) : new ArrayList<>()
        );
    }

    public static User toUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setName(userRequestDTO.name());
        user.setEmail(userRequestDTO.email());
        user.setPassword(userRequestDTO.password());
        user.setIncomes(new ArrayList<>());
        user.setExpenses(new ArrayList<>());
        return user;
    }

    public static UserResponseDTO toResponseDTO(UserEntity userEntity) {
        return new UserResponseDTO(
            userEntity.getId(),
            userEntity.getName(),
            userEntity.getEmail()
        );
    }

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    public static List<UserResponseDTO> toResponseDTOList(List<User> userList) {
        return userList.stream()
            .map(UserMapper::toResponseDTO)
            .toList();
    }
}
