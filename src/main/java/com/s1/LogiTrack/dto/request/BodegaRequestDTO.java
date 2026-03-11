package com.s1.LogiTrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BodegaRequestDTO (
        @NotBlank(message = "El nombre no puede estar vacio.")
        @Size(min = 2, max = 50, message = "Error, el rango del nombre debe estar entre 2 y 50 caracteres")
        String nombre,

        @NotBlank(message = "La ubicacion no puede estar vacia.")
        @Size(min = 2, max = 50, message = "Error, el rango de la ubicacion debe estar entre 2 y 50 caracteres")
        String ubicacion,

        @NotNull(message = "La capacidad no puede ser nula")
        @Positive(message = "La capacidad debe ser mayor a cero")
        Integer capacidad,

        @NotNull(message = "El ID del encargado no puede ser nulo")
        Long encargadoId
) {
}
