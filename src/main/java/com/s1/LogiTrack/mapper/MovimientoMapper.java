package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.response.MovimientoDetalleResponseDTO;
import com.s1.LogiTrack.dto.response.MovimientoResponseDTO;
import com.s1.LogiTrack.model.Movimiento;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimientoMapper {

  private final BodegaMapper bodegaMapper;
  private final MovimientoDetalleMapper movimientoDetalleMapper;

  public MovimientoMapper(BodegaMapper bodegaMapper, MovimientoDetalleMapper movimientoDetalleMapper){
      this.bodegaMapper = bodegaMapper;
      this.movimientoDetalleMapper = movimientoDetalleMapper;
  }

  public MovimientoResponseDTO entidadADTO(Movimiento movimiento){
      if (movimiento == null) return null;

      List<MovimientoDetalleResponseDTO> detalles = movimiento.getDetalles()
              .stream()
              .map(movimientoDetalleMapper::entidadDTO)
              .toList();

    return new MovimientoResponseDTO(
              movimiento.getId(),
              movimiento.getFecha(),
              movimiento.getTipoMovimiento(),
              movimiento.getUsuario() != null? movimiento.getUsuario().getNombre() : null,
              bodegaMapper.entidadADTO(movimiento.getBodegaOrigen()),
              bodegaMapper.entidadADTO(movimiento.getBodegaDestino()),
              detalles
      );
  }
}
