package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.MovimientoRequestDTO;
import com.s1.LogiTrack.dto.response.MovimientoResponseDTO;
import com.s1.LogiTrack.mapper.MovimientoMapper;
import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.Movimiento;
import com.s1.LogiTrack.model.Usuario;
import com.s1.LogiTrack.repository.BodegaRepository;
import com.s1.LogiTrack.repository.MovimientoRepository;
import com.s1.LogiTrack.repository.ProductoRepository;
import com.s1.LogiTrack.repository.UsuarioRepository;
import com.s1.LogiTrack.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoMapper movimientoMapper;


    @Override
    public MovimientoResponseDTO guardarMovimiento(MovimientoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario"));

        Bodega bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                .orElseThrow(() -> new RuntimeException("No existe la bodega origen"));

        Bodega bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                .orElseThrow(() -> new RuntimeException("No existe la bodega destino"));

        Movimiento movimiento = movimientoMapper.DTOAEntidad(dto, usuario, bodegaOrigen, bodegaDestino);
        Movimiento movimientoInsertado = movimientoRepository.save(movimiento);

        return movimientoMapper.entidadADTO(movimientoInsertado);
    }

    @Override
    public MovimientoResponseDTO actualizarMovimiento(MovimientoRequestDTO dto, Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho movimiento a actualizar"));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("No existe el usuario"));

        Bodega bodegaOrigen = bodegaRepository.findById(dto.bodegaOrigenId())
                .orElseThrow(() -> new RuntimeException("No existe la bodega origen"));

        Bodega bodegaDestino = bodegaRepository.findById(dto.bodegaDestinoId())
                .orElseThrow(() -> new RuntimeException("No existe la bodega destino"));

        movimientoMapper.actualizarEntidadDesdeDTO(movimiento, dto, usuario, bodegaOrigen, bodegaDestino);
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
