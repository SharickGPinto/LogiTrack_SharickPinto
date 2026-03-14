package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.request.MovimientoRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.dto.response.MovimientoDetalleResponseDTO;
import com.s1.LogiTrack.dto.response.MovimientoResponseDTO;
import com.s1.LogiTrack.model.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MovimientoMapper {

    public MovimientoResponseDTO entidadADTO(Movimiento movimiento) {
        if (movimiento == null) return null;

        List<MovimientoDetalleResponseDTO> detallesDTO = new ArrayList<>();

        if (movimiento.getDetalles() != null) {
            for (MovimientoDetalle detalle : movimiento.getDetalles()) {
                MovimientoDetalleResponseDTO detalleDTO = new MovimientoDetalleResponseDTO(
                        detalle.getId(),
                        detalle.getProducto() != null ? detalle.getProducto().getId() : null,
                        detalle.getProducto() != null ? detalle.getProducto().getNombre() : null,
                        detalle.getCantidad()
                );
                detallesDTO.add(detalleDTO);
            }
        }

        BodegaResponseDTO bodegaOrigenDTO = null;
        if (movimiento.getBodegaOrigen() != null) {
            bodegaOrigenDTO = new BodegaResponseDTO(
                    movimiento.getBodegaOrigen().getId(),
                    movimiento.getBodegaOrigen().getNombre(),
                    movimiento.getBodegaOrigen().getUbicacion(),
                    movimiento.getBodegaOrigen().getCapacidad(),
                    movimiento.getBodegaOrigen().getEncargado() != null
                            ? movimiento.getBodegaOrigen().getEncargado().getNombre()
                            : null
            );
        }

        BodegaResponseDTO bodegaDestinoDTO = null;
        if (movimiento.getBodegaDestino() != null) {
            bodegaDestinoDTO = new BodegaResponseDTO(
                    movimiento.getBodegaDestino().getId(),
                    movimiento.getBodegaDestino().getNombre(),
                    movimiento.getBodegaDestino().getUbicacion(),
                    movimiento.getBodegaDestino().getCapacidad(),
                    movimiento.getBodegaDestino().getEncargado() != null
                            ? movimiento.getBodegaDestino().getEncargado().getNombre()
                            : null
            );
        }

        return new MovimientoResponseDTO(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                movimiento.getUsuario() != null ? movimiento.getUsuario().getNombre() : null,
                bodegaOrigenDTO,
                bodegaDestinoDTO,
                detallesDTO
        );
    }

    public Movimiento DTOAEntidad(MovimientoRequestDTO dto, Usuario usuario, Bodega bodegaOrigen, Bodega bodegaDestino, List<MovimientoDetalle> detalles) {
        if (dto == null || usuario == null) return null;

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(dto.tipoMovimiento());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
        movimiento.setDetalles(detalles);

        return movimiento;
    }

    public void actualizarEntidadDesdeDTO(Movimiento movimiento, MovimientoRequestDTO dto, Usuario usuario, Bodega bodegaOrigen, Bodega bodegaDestino, List<MovimientoDetalle> detalles) {
        if (movimiento == null || dto == null || usuario == null) return;

        movimiento.setTipoMovimiento(dto.tipoMovimiento());
        movimiento.setUsuario(usuario);
        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
        movimiento.setDetalles(detalles);
    }
}