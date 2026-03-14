package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.request.MovimientoDetalleRequestDTO;
import com.s1.LogiTrack.dto.response.MovimientoDetalleResponseDTO;
import com.s1.LogiTrack.model.Movimiento;
import com.s1.LogiTrack.model.MovimientoDetalle;
import com.s1.LogiTrack.model.Producto;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class MovimientoDetalleMapper {

    public MovimientoDetalleResponseDTO entidadADTO(MovimientoDetalle detalle) {
        if (detalle == null) return null;

        return new MovimientoDetalleResponseDTO(
                detalle.getId(),
                detalle.getProducto() != null ? detalle.getProducto().getId() : null,
                detalle.getProducto() != null ? detalle.getProducto().getNombre() : null,
                detalle.getCantidad()
        );
    }

    public MovimientoDetalle DTOAEntidad(MovimientoDetalleRequestDTO dto, Producto producto, Movimiento movimiento) {
        if (dto == null || producto == null || movimiento == null) return null;

        MovimientoDetalle detalle = new MovimientoDetalle();
        detalle.setProducto(producto);
        detalle.setMovimiento(movimiento);
        detalle.setCantidad(dto.cantidad());

        return detalle;
    }

    public void actualizarEntidadDesdeDTO(MovimientoDetalle detalle, MovimientoDetalleRequestDTO dto, Producto producto) {
        if (detalle == null || dto == null || producto == null) return;

        detalle.setProducto(producto);
        detalle.setCantidad(dto.cantidad());
    }
}