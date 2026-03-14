package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.BodegaRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.mapper.BodegaMapper;
import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.Usuario;
import com.s1.LogiTrack.repository.BodegaRepository;
import com.s1.LogiTrack.repository.UsuarioRepository;
import com.s1.LogiTrack.service.BodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    public BodegaResponseDTO guardarBodega(BodegaRequestDTO dto) {
        Usuario encargado = usuarioRepository.findById(dto.encargadoId())
                .orElseThrow(() -> new RuntimeException("No existe el encargado"));

        Bodega b = bodegaMapper.DTOAEntidad(dto, encargado);
        Bodega bInsertada = bodegaRepository.save(b);
        return bodegaMapper.entidadADTO(bInsertada);
    }

    @Override
    public BodegaResponseDTO actualizarBodega(BodegaRequestDTO dto, Long id) {
        Bodega b = bodegaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicha bodega a actualizar"));

        Usuario encargado = usuarioRepository.findById(dto.encargadoId())
                .orElseThrow(() -> new RuntimeException("No existe el encargado"));

        bodegaMapper.actualizarEntidadDesdeDTO(b, dto, encargado);
        Bodega bActualizada = bodegaRepository.save(b);
        return bodegaMapper.entidadADTO(bActualizada);
    }

    @Override
    public void eliminarBodega(Long id) {
        Bodega b = bodegaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicha bodega a eliminar"));
        bodegaRepository.delete(b);
    }

    @Override
    public List<BodegaResponseDTO> listarBodega() {
        List<Bodega> bodegas = bodegaRepository.findAll();
        return bodegas.stream().map(bodegaMapper::entidadADTO).toList();

    }

    @Override
    public BodegaResponseDTO buscarPorId(Long id) {
        Bodega b = bodegaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicha bodega"));
        return bodegaMapper.entidadADTO(b);
    }
}
