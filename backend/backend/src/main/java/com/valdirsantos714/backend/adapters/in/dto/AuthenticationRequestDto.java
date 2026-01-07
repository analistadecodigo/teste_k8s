package com.valdirsantos714.backend.adapters.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequestDto (
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password
) {
}