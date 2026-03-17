package com.s1.LogiTrack.dto.response;

public record BodegaResponseDTO(
        Long id, String nombre, String ubicacion, Integer capacidad, String nombreEncargado
) {
}

// DTO que devuelve los datos de una bodega después de registrar o consultar la información.