package com.semillitas.backend.salon;

import jakarta.validation.constraints.*;

public record SalonRequest(
    @NotBlank @Size(max = 50) String nombre,
    @NotNull @Min(3) @Max(5) Integer edad,
    @NotBlank @Pattern(regexp = "MANANA|TARDE", message = "Turno debe ser MANANA o TARDE") String turno,
    @NotNull @Min(1) @Max(40) Integer capacidad,
    Long docenteId) {}

