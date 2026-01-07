package com.valdirsantos714.backend.adapters.in.controller;

import com.valdirsantos714.backend.adapters.out.dto.FinancialSummaryResponseDTO;
import com.valdirsantos714.backend.application.service.FinancialSummaryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/financial-summary")
@CrossOrigin("*")
public class FinancialSummaryController {

    private final FinancialSummaryServiceImpl service;

    public FinancialSummaryController(FinancialSummaryServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{email}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Retorna o resumo financeiro de um usuário por email",  responses = {
            @ApiResponse(description = "Requisição feita com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<FinancialSummaryResponseDTO> getFinancialSummary(@PathVariable String email) {
        return ResponseEntity.ok(service.getFinancialSummary(email));
    }
}
