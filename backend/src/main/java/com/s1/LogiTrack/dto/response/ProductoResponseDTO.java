package com.s1.LogiTrack.dto.response;

public record ProductoResponseDTO(
        Long id, String nombre, String categoria, Double precio,
        Integer stock, BodegaResponseDTO bodega
) {
}
