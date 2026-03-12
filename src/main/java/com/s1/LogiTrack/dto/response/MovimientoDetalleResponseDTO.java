package com.s1.LogiTrack.dto.response;

import com.s1.LogiTrack.model.Movimiento;
import com.s1.LogiTrack.model.Producto;

public record MovimientoDetalleResponseDTO(
        Long id, Long productoId, String nombreProducto, Integer cantidad

) {
}
