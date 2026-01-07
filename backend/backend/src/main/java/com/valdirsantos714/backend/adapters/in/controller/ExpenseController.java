package com.valdirsantos714.backend.adapters.in.controller;

import com.valdirsantos714.backend.adapters.in.dto.ExpenseRequestDTO;
import com.valdirsantos714.backend.adapters.out.dto.ExpenseResponseDTO;
import com.valdirsantos714.backend.adapters.out.repository.mapper.ExpenseMapper;
import com.valdirsantos714.backend.application.core.domain.Expense;
import com.valdirsantos714.backend.application.service.ExpenseServiceImpl;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin("*")
public class ExpenseController {

    private final ExpenseServiceImpl service;

    public ExpenseController(ExpenseServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/{email}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Cria uma nova despesa para um usuário",  responses = {
            @ApiResponse(description = "Despesa criada com sucesso", responseCode = "201"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ExpenseResponseDTO> createExpense(@PathVariable String email, @RequestBody @Valid ExpenseRequestDTO expenseRequestDTO) {
        Expense expense = service.save(email, expenseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExpenseMapper.toResponse(expense));
    }

    @GetMapping
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Retorna lista de todas as despesas",  responses = {
            @ApiResponse(description = "Requisição feita com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses() {
        List<Expense> expenses = service.findAll();
        return ResponseEntity.ok().body(ExpenseMapper.toExpenseResponseDTOList(expenses));
    }

    @GetMapping("/{email}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Retorna lista de despesas por email do usuário",  responses = {
            @ApiResponse(description = "Requisição feita com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByUserEmail(@PathVariable String email) {
        List<Expense> expenses = service.findByUserEmail(email);
        return ResponseEntity.ok().body(
                expenses.stream()
                        .map(ExpenseMapper::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{email}/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Atualiza uma despesa existente",  responses = {
            @ApiResponse(description = "Despesa atualizada com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable String email,
            @PathVariable Long id,
            @RequestBody @Valid ExpenseRequestDTO expenseRequestDTO) {
        Expense updatedExpense = service.update(id, email, expenseRequestDTO);
        return ResponseEntity.ok().body(ExpenseMapper.toResponse(updatedExpense));
    }

    @DeleteMapping("/{email}/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "Deleta uma despesa existente",  responses = {
            @ApiResponse(description = "Despesa deletada com sucesso", responseCode = "204"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> deleteExpense(@PathVariable String email, @PathVariable Long id) {
        service.delete(email, id);
        return ResponseEntity.noContent().build();
    }

}
