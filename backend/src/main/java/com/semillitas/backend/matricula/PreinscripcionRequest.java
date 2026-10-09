package com.semillitas.backend.matricula;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PreinscripcionRequest(
    @NotBlank @Pattern(regexp = "\\d{8}", message = "DNI del niño: 8 dígitos") String dniNino,
    @NotBlank @Size(max = 80) String nombres,
    @NotBlank @Size(max = 80) String apellidos,
    @Past LocalDate fechaNacimiento,
    @NotBlank @Pattern(regexp = "\\d{8}", message = "DNI del apoderado: 8 dígitos") String dniApoderado) {}
