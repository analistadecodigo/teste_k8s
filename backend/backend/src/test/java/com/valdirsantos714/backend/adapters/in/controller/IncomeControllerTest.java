package com.valdirsantos714.backend.adapters.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valdirsantos714.backend.adapters.in.dto.IncomeRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.mapper.IncomeMapper;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.core.domain.enums.IncomeCategory;
import com.valdirsantos714.backend.application.service.IncomeServiceImpl;
import com.valdirsantos714.backend.configuration.TestSecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
@WebMvcTest(IncomeController.class)
class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncomeServiceImpl service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should create a new income and return 201 status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateIncome() throws Exception {
        // Given
        String email = "admin@email.com";
        IncomeRequestDTO requestDTO = new IncomeRequestDTO(
                "Test Income",
                "Description",
                500.0,
                LocalDate.now(),
                IncomeCategory.SALES.name()
        );

        Income incomeDomain = IncomeMapper.toIncome(requestDTO);
        incomeDomain.setId(1L); 

        given(service.save(eq(email), any(IncomeRequestDTO.class)))
                .willReturn(incomeDomain);

        // When + Then
        mockMvc.perform(post("/api/incomes/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(requestDTO.name()));
    }

    @Test
    @DisplayName("should return all incomes")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetAllIncomes() throws Exception {
        // Given
        Income income = new Income();
        income.setId(1L);
        income.setName("Income 1");

        given(service.findAll()).willReturn(List.of(income));

        // When + Then
        mockMvc.perform(get("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Income 1"));
    }

    @Test
    @DisplayName("should get incomes by user email")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetIncomesByUserEmail() throws Exception {
        // Given
        String email = "admin@email.com";
        Income income = new Income();
        income.setId(2L);
        income.setName("Income 2");

        given(service.findByUserEmail(email)).willReturn(List.of(income));

        // When + Then
        mockMvc.perform(get("/api/incomes/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Income 2"));
    }

    @Test
    @DisplayName("should update an existing income")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateIncome() throws Exception {
        // Given
        String email = "admin@email.com";
        Long id = 1L;
        IncomeRequestDTO requestDTO = new IncomeRequestDTO(
                "Updated Income",
                "Updated Desc",
                600.0,
                LocalDate.now(),
                IncomeCategory.COMMISSION.name()
        );

        Income updatedIncome = IncomeMapper.toIncome(requestDTO);
        updatedIncome.setId(id);

        given(service.update(eq(id), eq(email), any(IncomeRequestDTO.class)))
                .willReturn(updatedIncome);

        // When + Then
        mockMvc.perform(put("/api/incomes/{email}/{id}", email, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Income"));
    }

    @Test
    @DisplayName("should delete an income and return 204 status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldDeleteIncome() throws Exception {
        // Given
        String email = "admin@email.com";
        Long id = 1L;

        // When + Then
        mockMvc.perform(delete("/api/incomes/{email}/{id}", email, id))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(email, id);
    }
}
