package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.UsuarioRequestDTO;
import com.s1.LogiTrack.dto.response.UsuarioResponseDTO;
import com.s1.LogiTrack.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {


    @Override
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO dto) {
        return null;
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(UsuarioRequestDTO dto, Long id) {
        return null;
    }

    @Override
    public void eliminarUsuario(Long id) {

    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return List.of();
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        return null;
    }
}
