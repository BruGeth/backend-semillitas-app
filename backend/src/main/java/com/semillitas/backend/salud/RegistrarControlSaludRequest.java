package com.semillitas.backend.salud;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarControlSaludRequest(
    @NotNull Long ninoId,
    @NotBlank @Size(max = 30) String tipo,
    @NotNull LocalDate fecha,
    @DecimalMin(value = "0.0") @DecimalMax(value = "150.0") BigDecimal pesoKg,
    @DecimalMin(value = "0.0") @DecimalMax(value = "200.0") BigDecimal tallaCm,
    LocalDate proximaFecha,
    Boolean alerta,
    String observacion) {}

