package com.s1.LogiTrack.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimientoDetalleRequestDTO(
        @NotNull(message = "El id del producto no puede ser nulo")
        Long productoID,

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser mayor a cero")
    Integer cantidad
) {
}
