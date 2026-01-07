package com.valdirsantos714.backend.application.usecase;

import com.valdirsantos714.backend.adapters.out.dto.FinancialSummaryResponseDTO;

public interface FinancialSummaryUseCases {
    FinancialSummaryResponseDTO getFinancialSummary(String email);
}
