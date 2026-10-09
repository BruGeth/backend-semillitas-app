package com.semillitas.backend.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos numéricos") String dni,
    @NotBlank @Size(min = 6, max = 64, message = "Contraseña entre 6 y 64 caracteres") String password) {}
