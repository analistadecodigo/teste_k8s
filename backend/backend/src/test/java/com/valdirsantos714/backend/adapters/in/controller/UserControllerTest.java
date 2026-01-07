package com.valdirsantos714.backend.adapters.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valdirsantos714.backend.adapters.in.dto.UserRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.mapper.UserMapper;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.core.domain.enums.UserRole;
import com.valdirsantos714.backend.application.service.UserServiceImpl;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfiguration.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("should create a new user and return 201 status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateUser() throws Exception {
        // Given
        UserRequestDTO requestDTO = new UserRequestDTO(
                "Test User",
                "test@example.com",
                "password123"
        );

        User userDomain = UserMapper.toUser(requestDTO);
        userDomain.setId(1L); 
        userDomain.setRole(UserRole.USER);

        given(service.save(any(UserRequestDTO.class)))
                .willReturn(userDomain);

        // When + Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(requestDTO.name()))
                .andExpect(jsonPath("$.email").value(requestDTO.email()));
    }

    @Test
    @DisplayName("should return all users")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldGetAllUsers() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("User 1");
        user.setEmail("user1@example.com");
        user.setRole(UserRole.USER);

        given(service.findAll()).willReturn(List.of(user));

        // When + Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("User 1"));
    }

    @Test
    @DisplayName("should update an existing user")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldUpdateUser() throws Exception {
        // Given
        Long id = 1L;
        UserRequestDTO requestDTO = new UserRequestDTO(
                "Updated User",
                "updated@example.com",
                "newpassword"
        );

        User updatedUser = UserMapper.toUser(requestDTO);
        updatedUser.setId(id);
        updatedUser.setRole(UserRole.ADMIN);

        given(service.update(eq(id), any(UserRequestDTO.class)))
                .willReturn(updatedUser);

        // When + Then
        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @DisplayName("should delete a user and return 204 status")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldDeleteUser() throws Exception {
        // Given
        Long id = 1L;

        // When + Then
        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        Mockito.verify(service).delete(id);
    }
}
