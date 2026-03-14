package com.s1.LogiTrack.dto.request;

import jakarta.validation.constraints.*;

public record ProductoRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacio.")
        @Size(min = 2, max = 120, message = "Error, el rango del nombre debe estar entre 2 y 120 caracteres")
        String nombre,

        String categoria,

        @NotNull(message = "El precio no puede ser nulo.")
        @Positive(message = "El precio debe ser mayor a cero.")
        Double precio,

        @NotNull(message = "El stock no puede ser nulo.")
        @Min(value = 0, message = "El stock no puede ser negativo.")
        Integer stock,

        @NotNull(message = "El ID de la bodega no puede ser nulo.")
        Long bodegaId
) {
}
