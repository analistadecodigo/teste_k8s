package com.valdirsantos714.backend.adapters.out.repository.mapper;

import com.valdirsantos714.backend.adapters.in.dto.IncomeRequestDTO;
import com.valdirsantos714.backend.adapters.out.dto.IncomeResponseDTO;
import com.valdirsantos714.backend.adapters.out.repository.entity.IncomeEntity;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.core.domain.enums.IncomeCategory;

import java.util.List;

public class IncomeMapper {

    public static Income toIncome(IncomeEntity incomeEntity) {
        Income income = new Income();
        income.setId(incomeEntity.getId());
        income.setName(incomeEntity.getName());
        income.setDescription(incomeEntity.getDescription());
        income.setAmount(incomeEntity.getAmount());
        income.setDate(incomeEntity.getDate());
        income.setCategory(incomeEntity.getCategory());

        if (incomeEntity.getUser() != null) {
            User user = new User();
            user.setId(incomeEntity.getUser().getId());
            user.setEmail(incomeEntity.getUser().getEmail());
            income.setUser(user);
        }

        return income;
    }

    public static Income toIncome(IncomeRequestDTO dto) {
        Income income = new Income();
        income.setName(dto.name());
        income.setDescription(dto.description());
        income.setAmount(dto.amount());
        income.setDate(dto.date());
        income.setCategory(IncomeCategory.valueOf(dto.category()));
        return income;
    }

    public static IncomeEntity toIncomeEntity(Income income) {
        IncomeEntity entity = new IncomeEntity();
        entity.setId(income.getId());
        entity.setName(income.getName());
        entity.setDescription(income.getDescription());
        entity.setAmount(income.getAmount());
        entity.setDate(income.getDate());
        entity.setCategory(income.getCategory());

        if (income.getUser() != null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(income.getUser().getId());
            userEntity.setEmail(income.getUser().getEmail());
            entity.setUser(userEntity);
        }

        return entity;
    }

    public static List<IncomeResponseDTO> toIncomeResponseDTOList(List<Income> incomes) {
        return incomes.stream()
                .map(IncomeMapper::toResponseDTO)
                .toList();
    }

    public static IncomeResponseDTO toResponseDTO(Income income) {
        return new IncomeResponseDTO(
            income.getId(),
            income.getName(),
            income.getDescription(),
            income.getAmount(),
            income.getDate(),
            income.getUser() != null ? income.getUser().getId() : null,
            income.getCategory()
        );
    }

    public static List<IncomeEntity> toIncomeEntityList(List<Income> incomes) {
        return incomes.stream()
                .map(IncomeMapper::toIncomeEntity)
                .toList();
    }

    public static List<Income> toIncomeList(List<IncomeEntity> incomes) {
        return incomes.stream()
                .map(IncomeMapper::toIncome)
                .toList();
    }
}
