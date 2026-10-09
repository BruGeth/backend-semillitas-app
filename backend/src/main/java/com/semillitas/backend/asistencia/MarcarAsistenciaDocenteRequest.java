package com.semillitas.backend.asistencia;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record MarcarAsistenciaDocenteRequest(
    @NotNull LocalDate fecha,
    LocalTime horaEntrada,
    LocalTime horaSalida) {}

