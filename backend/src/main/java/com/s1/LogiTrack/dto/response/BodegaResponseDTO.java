package com.s1.LogiTrack.dto.response;

public record BodegaResponseDTO(
        Long id, String nombre, String ubicacion, Integer capacidad, String nombreEncargado
) {
}
