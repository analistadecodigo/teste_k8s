package com.valdirsantos714.backend.adapters.in.controller;

import com.valdirsantos714.backend.adapters.in.dto.AuthenticationRequestDto;
import com.valdirsantos714.backend.adapters.in.dto.DadosToken;
import com.valdirsantos714.backend.adapters.in.dto.UserRequestDTO;
import com.valdirsantos714.backend.adapters.out.dto.UserResponseDTO;
import com.valdirsantos714.backend.adapters.out.repository.entity.UserEntity;
import com.valdirsantos714.backend.adapters.out.repository.mapper.UserMapper;
import com.valdirsantos714.backend.application.service.UserServiceImpl;
import com.valdirsantos714.backend.infrastructure.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationManager manager;

    private final TokenService tokenService;

    private final UserServiceImpl service;

    public AuthenticationController(AuthenticationManager manager, TokenService tokenService, UserServiceImpl service) {
        this.manager = manager;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Operation(summary = "Faz login do usuário",  responses = {
            @ApiResponse(description = "Requisição feita com sucesso", responseCode = "200"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<DadosToken> efetuarLogin(@RequestBody @Valid AuthenticationRequestDto dto){
        log.info("Recebendo dados de autenticação: {} ", dto.email());
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var authentication = manager.authenticate(authenticationToken);
        log.info("{}", authentication.getPrincipal());
        var tokenJWT = tokenService.geraToken((UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(new DadosToken(tokenJWT));
    }

    @Operation(summary = "Salva usuário",  responses = {
            @ApiResponse(description = "Requisição feita com sucesso", responseCode = "201"),
            @ApiResponse(responseCode = "401", description = "Erro de autenticação"),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> efetuarCadastro(@RequestBody @Valid UserRequestDTO dto, UriComponentsBuilder uriBuilder){
        var user = service.save(dto);
        var uri = uriBuilder.path("/admin/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(UserMapper.toResponseDTO(user));
    }
}
