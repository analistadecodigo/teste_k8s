package com.valdirsantos714.backend.adapters.in.controller;

import com.valdirsantos714.backend.adapters.in.dto.IncomeRequestDTO;
import com.valdirsantos714.backend.adapters.out.dto.IncomeResponseDTO;
import com.valdirsantos714.backend.adapters.out.repository.mapper.IncomeMapper;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.service.IncomeServiceImpl;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@CrossOrigin("*")
public class IncomeController {

    private final IncomeServiceImpl service;

    public IncomeController(IncomeServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/{email}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Cria uma nova receita para um usuário",  responses = {
            @ApiResponse(description = "Receita criada com sucesso", responseCode = "201"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<IncomeResponseDTO> save(@PathVariable String email, @RequestBody @Valid IncomeRequestDTO dto) {
        Income income = service.save(email, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(IncomeMapper.toResponseDTO(income));
    }

    @GetMapping
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Retorna lista de todas as receitas",  responses = {
            @ApiResponse(description = "Requisição feita com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<IncomeResponseDTO>> getAll() {
        return ResponseEntity.ok().body(IncomeMapper.toIncomeResponseDTOList(service.findAll()));
    }

    @GetMapping("/{email}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Retorna lista de receitas por email do usuário",  responses = {
            @ApiResponse(description = "Requisição feita com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<IncomeResponseDTO>> getByUserEmail(@PathVariable String email) {
        List<Income> incomes = service.findByUserEmail(email);
        return ResponseEntity.ok().body(
                incomes.stream()
                        .map(IncomeMapper::toResponseDTO)
                        .toList()
        );
    }

    @PutMapping("/{email}/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Atualiza uma receita existente",  responses = {
            @ApiResponse(description = "Receita atualizada com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<IncomeResponseDTO> update(
            @PathVariable String email,
            @PathVariable Long id,
            @RequestBody @Valid IncomeRequestDTO dto) {
        Income income = service.update(id, email, dto);
        return ResponseEntity.ok().body(IncomeMapper.toResponseDTO(income));
    }

    @DeleteMapping("/{email}/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Deleta uma receita existente",  responses = {
            @ApiResponse(description = "Receita deletada com sucesso", responseCode = "204"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> delete(@PathVariable String email, @PathVariable Long id) {
        service.delete(email, id);
        return ResponseEntity.noContent().build();
    }
}
