package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.request.MovimientoDetalleRequestDTO;
import com.s1.LogiTrack.dto.response.MovimientoDetalleResponseDTO;
import com.s1.LogiTrack.model.MovimientoDetalle;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class MovimientoDetalleMapper {

public MovimientoDetalleResponseDTO entidaDTO(MovimientoDetalle entidad ) {
    if (entidad == null) return null;

   return new MovimientoDetalleResponseDTO(
    entidad.getId(),
    entidad.getProducto() != null ? entidad.getProducto().getId() : null,
    entidad.getProducto() != null ? entidad.getProducto().getNombre() : null,
    entidad.getCantidad()
    );

}
}
