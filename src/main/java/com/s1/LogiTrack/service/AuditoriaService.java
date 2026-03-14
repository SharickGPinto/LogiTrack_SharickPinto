package com.s1.LogiTrack.service;

import com.s1.LogiTrack.dto.response.AuditoriaResponseDTO;
import com.s1.LogiTrack.model.OperacionAuditoria;

import java.util.List;

public interface AuditoriaService {
    List<AuditoriaResponseDTO> listarAuditorias();

    AuditoriaResponseDTO buscarPorId(Long id);

    List<AuditoriaResponseDTO> buscarPorNombreUsuario(String nombre);

    List<AuditoriaResponseDTO> buscarPorOperacion(OperacionAuditoria operacion);
}
