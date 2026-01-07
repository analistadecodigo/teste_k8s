package com.valdirsantos714.backend.adapters.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valdirsantos714.backend.adapters.in.dto.AuthenticationRequestDto;
import com.valdirsantos714.backend.adapters.in.dto.UserRequestDTO;
import com.valdirsantos714.backend.adapters.out.repository.UserRepositoryAdapter;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.UserMapper;
import com.valdirsantos714.backend.application.core.domain.User;
import com.valdirsantos714.backend.application.core.domain.enums.UserRole;
import com.valdirsantos714.backend.application.service.UserServiceImpl;
import com.valdirsantos714.backend.configuration.TestSecurityConfiguration;
import com.valdirsantos714.backend.infrastructure.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(TestSecurityConfiguration.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    @DisplayName("should authenticate user and return 200 status with token")
    void shouldAuthenticateUser() throws Exception {
        // Given
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto("test@example.com", "password123");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password());
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(requestDto.email());
        userEntity.setPassword(requestDto.password()); // In a real scenario, this would be encoded
        userEntity.setRole(UserRole.USER);

        Authentication authentication = Mockito.mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(userEntity);
        given(authenticationManager.authenticate(authenticationToken)).willReturn(authentication);
        given(tokenService.geraToken(any(UserEntity.class))).willReturn("mocked-jwt-token");

        // When + Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenJWT").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("should return 401 for invalid login credentials")
    void shouldReturn401ForInvalidLogin() throws Exception {
        // Given
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto("invalid@example.com", "wrongpassword");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password());

        given(authenticationManager.authenticate(authenticationToken))
                .willThrow(new BadCredentialsException("Invalid credentials"));

        // When + Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should register a new user and return 201 status")
    void shouldRegisterUser() throws Exception {
        // Given
        UserRequestDTO requestDTO = new UserRequestDTO(
                "New User",
                "newuser@example.com",
                "newpassword123"
        );

        User userDomain = UserMapper.toUser(requestDTO);
        userDomain.setId(1L);
        userDomain.setRole(UserRole.USER);

        given(userService.save(any(UserRequestDTO.class)))
                .willReturn(userDomain);

        // When + Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(requestDTO.name()))
                .andExpect(jsonPath("$.email").value(requestDTO.email()));
    }
}
