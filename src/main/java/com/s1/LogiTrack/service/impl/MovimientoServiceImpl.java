package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.MovimientoDetalleRequestDTO;
import com.s1.LogiTrack.dto.request.MovimientoRequestDTO;
import com.s1.LogiTrack.dto.response.MovimientoResponseDTO;
import com.s1.LogiTrack.mapper.MovimientoDetalleMapper;
import com.s1.LogiTrack.mapper.MovimientoMapper;
import com.s1.LogiTrack.model.*;
import com.s1.LogiTrack.repository.BodegaRepository;
import com.s1.LogiTrack.repository.MovimientoRepository;
import com.s1.LogiTrack.repository.ProductoRepository;
import com.s1.LogiTrack.repository.UsuarioRepository;
import com.s1.LogiTrack.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {
    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final MovimientoDetalleMapper movimientoDetalleMapper;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;


    @Override
    public MovimientoResponseDTO guardarMovimiento(MovimientoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario"));

        Bodega bodegaOrigen = null;
        if (dto.bodegaOrigenId() != null) {
            bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                    .orElseThrow(() -> new RuntimeException("No existe la bodega origen"));
        }
        Bodega bodegaDestino = null;
        if (dto.bodegaDestinoId() != null) {
            bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                    .orElseThrow(() -> new RuntimeException("No existe la bodega destino"));
        }
        List<MovimientoDetalle> detalles = new ArrayList<>();

        Movimiento movimiento = movimientoMapper.DTOAEntidad(dto, usuario, bodegaOrigen, bodegaDestino, detalles);
        for (MovimientoDetalleRequestDTO detalleDTO : dto.detalles()) {
            Producto producto = productoRepository.findById(detalleDTO.productoId())
                    .orElseThrow(() -> new RuntimeException("No existe el producto"));

            MovimientoDetalle detalle = movimientoDetalleMapper.DTOAEntidad(detalleDTO, producto, movimiento);
            detalles.add(detalle);
        }
        Movimiento movimientoInsertado = movimientoRepository.save(movimiento);
        return movimientoMapper.entidadADTO(movimientoInsertado);
    }

    @Override
    public MovimientoResponseDTO actualizarMovimiento(MovimientoRequestDTO dto, Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho movimiento a actualizar"));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario"));

        Bodega bodegaOrigen = null;
        if (dto.bodegaOrigenId() != null) {
            bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                    .orElseThrow(() -> new RuntimeException("No existe la bodega origen"));
        }

        Bodega bodegaDestino = null;
        if (dto.bodegaDestinoId() != null) {
            bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                    .orElseThrow(() -> new RuntimeException("No existe la bodega destino"));
        }

        List<MovimientoDetalle> detalles = new ArrayList<>();

        for (MovimientoDetalleRequestDTO detalleDTO : dto.detalles()) {
            Producto producto = productoRepository.findById(detalleDTO.productoId())
                    .orElseThrow(() -> new RuntimeException("No existe el producto"));

            MovimientoDetalle detalle = movimientoDetalleMapper.DTOAEntidad(detalleDTO, producto, movimiento);
            detalles.add(detalle);
        }

        movimientoMapper.actualizarEntidadDesdeDTO(movimiento, dto, usuario, bodegaOrigen, bodegaDestino, detalles);
        Movimiento movimientoActualizado = movimientoRepository.save(movimiento);

        return movimientoMapper.entidadADTO(movimientoActualizado);
    }


    @Override
    public List<MovimientoResponseDTO> listarMovimientos() {
        return movimientoRepository.findAll().stream()
                .map(movimientoMapper::entidadADTO)
                .toList();
    }

    @Override
    public MovimientoResponseDTO buscarPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho movimiento"));

        return movimientoMapper.entidadADTO(movimiento);
    }

    @Override
    public void eliminarMovimiento(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el movimiento a eliminar"));

        movimientoRepository.delete(movimiento);
    }
}
