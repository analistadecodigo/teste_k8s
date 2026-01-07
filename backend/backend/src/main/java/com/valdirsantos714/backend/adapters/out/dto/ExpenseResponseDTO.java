package com.valdirsantos714.backend.adapters.out.dto;

import com.valdirsantos714.backend.application.core.domain.enums.ExpenseCategory;
import java.time.LocalDate;

public record ExpenseResponseDTO(
    Long id,
    String name,
    String description,
    Double amount,
    LocalDate date,
    ExpenseCategory category,
    Long userId
) {}
