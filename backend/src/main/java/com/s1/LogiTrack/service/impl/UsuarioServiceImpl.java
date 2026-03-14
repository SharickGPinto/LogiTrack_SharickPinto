package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.UsuarioRequestDTO;
import com.s1.LogiTrack.dto.response.UsuarioResponseDTO;
import com.s1.LogiTrack.mapper.UsuarioMapper;
import com.s1.LogiTrack.model.Usuario;
import com.s1.LogiTrack.repository.UsuarioRepository;
import com.s1.LogiTrack.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByUsername(dto.username()) != null) {
            throw new RuntimeException("Ya existe un usuario con ese username");
        }

        if (usuarioRepository.findByDocumento(dto.documento()) != null) {
            throw new RuntimeException("Ya existe un usuario con ese documento");
        }

        Usuario usuario = usuarioMapper.DTOAEntidad(dto);
        Usuario usuarioInsertado = usuarioRepository.save(usuario);

        return usuarioMapper.entidadADTO(usuarioInsertado);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(UsuarioRequestDTO dto, Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho usuario a actualizar"));

        Usuario usuarioPorUsername = usuarioRepository.findByUsername(dto.username());
        if (usuarioPorUsername != null && !usuarioPorUsername.getId().equals(id)) {
            throw new RuntimeException("Ya existe otro usuario con ese username");
        }

        Usuario usuarioPorDocumento = usuarioRepository.findByDocumento(dto.documento());
        if (usuarioPorDocumento != null && !usuarioPorDocumento.getId().equals(id)) {
            throw new RuntimeException("Ya existe otro usuario con ese documento");
        }

        usuarioMapper.actualizarEntidadDesdeDTO(usuario, dto);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.entidadADTO(usuarioActualizado);
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho usuario a eliminar"));

        usuarioRepository.delete(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::entidadADTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho usuario"));

        return usuarioMapper.entidadADTO(usuario);

    }
}
