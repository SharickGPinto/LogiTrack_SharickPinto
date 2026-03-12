package com.s1.LogiTrack.dto.response;

import com.s1.LogiTrack.model.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public record MovimientoResponseDTO(
        Long id, LocalDateTime fecha, TipoMovimiento tipoMovimiento, String nombreusuario, BodegaResponseDTO bodegaOrigen, BodegaResponseDTO bodegaDestino,
        List<MovimientoDetalleResponseDTO> detalles

) {
}
