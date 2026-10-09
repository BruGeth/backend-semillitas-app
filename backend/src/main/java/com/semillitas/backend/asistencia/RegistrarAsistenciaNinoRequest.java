package com.semillitas.backend.asistencia;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record RegistrarAsistenciaNinoRequest(
    @NotNull Long ninoId,
    @NotNull Long salonId,
    @NotNull LocalDate fecha,
    LocalTime hora,
    @NotBlank @Pattern(regexp = "MANANA|TARDE", message = "Turno debe ser MANANA o TARDE") String turno,
    @NotBlank @Pattern(regexp = "ASISTIO|TARDANZA|FALTA|JUSTIFICADO", message = "Estado inválido") String estado) {}

