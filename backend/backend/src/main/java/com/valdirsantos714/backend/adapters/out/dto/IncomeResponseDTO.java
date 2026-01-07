package com.valdirsantos714.backend.adapters.out.dto;

import com.valdirsantos714.backend.application.core.domain.enums.IncomeCategory;

import java.time.LocalDate;

public record IncomeResponseDTO(
        Long id,
        String name,
        String description,
        Double amount,
        LocalDate date,
        Long userId,
        IncomeCategory category
) {}
