package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.response.AuditoriaResponseDTO;
import com.s1.LogiTrack.mapper.AuditoriaMapper;
import com.s1.LogiTrack.model.Auditoria;
import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.repository.AuditoriaRepository;
import com.s1.LogiTrack.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;

    @Override
    public List<AuditoriaResponseDTO> listarAuditorias() {
        return auditoriaRepository.findAll().stream()
                .map(auditoriaMapper::entidadADTO)
                .toList();
    }

    @Override
    public AuditoriaResponseDTO buscarPorId(Long id) {
        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicha auditoría"));

        return auditoriaMapper.entidadADTO(auditoria);
    }

    @Override
    public List<AuditoriaResponseDTO> buscarPorNombreUsuario(String nombre) {
        return auditoriaRepository.findByUsuario_NombreIgnoreCase(nombre).stream()
                .map(auditoriaMapper::entidadADTO)
                .toList();
    }

    @Override
    public List<AuditoriaResponseDTO> buscarPorOperacion(OperacionAuditoria operacion) {
        return auditoriaRepository.findByOperacion(operacion).stream()
                .map(auditoriaMapper::entidadADTO)
                .toList();
    }
}
