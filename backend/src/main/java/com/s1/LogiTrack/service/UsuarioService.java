package com.s1.LogiTrack.service;

import com.s1.LogiTrack.dto.request.UsuarioRequestDTO;
import com.s1.LogiTrack.dto.response.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO dto);

    UsuarioResponseDTO actualizarUsuario(UsuarioRequestDTO dto, String documento);

    void eliminarUsuario(String docuemento);

    List<UsuarioResponseDTO> listarUsuarios();

    UsuarioResponseDTO buscarPorDocumento(String documento);
}
