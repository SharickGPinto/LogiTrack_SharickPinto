package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.BodegaRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.exception.BusinessRuleException;
import com.s1.LogiTrack.mapper.BodegaMapper;
import com.s1.LogiTrack.model.Auditoria;
import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.model.Usuario;
import com.s1.LogiTrack.repository.AuditoriaRepository;
import com.s1.LogiTrack.repository.BodegaRepository;
import com.s1.LogiTrack.repository.UsuarioRepository;
import com.s1.LogiTrack.service.BodegaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaRepository auditoriaRepository;

    @Override
    public BodegaResponseDTO guardarBodega(BodegaRequestDTO dto) {
        Usuario encargado = usuarioRepository.findById(dto.encargadoId())
                .orElseThrow(() -> new EntityNotFoundException("No existe el encargado"));

        if (bodegaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessRuleException("Ya existe una bodega con ese nombre");
        }

        Bodega bodega = bodegaMapper.DTOAEntidad(dto, encargado);
        Bodega bodegaInsertada = bodegaRepository.save(bodega);

        registrarAuditoria("Bodega", OperacionAuditoria.INSERT, encargado, null, construirResumen(bodegaInsertada));
        return bodegaMapper.entidadADTO(bodegaInsertada);
    }

    @Override
    public BodegaResponseDTO actualizarBodega(BodegaRequestDTO dto, Long id) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicha bodega a actualizar"));

        Usuario encargado = usuarioRepository.findById(dto.encargadoId())
                .orElseThrow(() -> new EntityNotFoundException("No existe el encargado"));

        List<Bodega> bodegasConMismoNombre = bodegaRepository.findByNombreIgnoreCase(dto.nombre());
        boolean existeOtra = bodegasConMismoNombre.stream()
                .anyMatch(item -> !item.getId().equals(bodega.getId()));
        if (existeOtra) {
            throw new BusinessRuleException("Ya existe otra bodega con ese nombre");
        }

        String valorAnterior = construirResumen(bodega);
        bodegaMapper.actualizarEntidadDesdeDTO(bodega, dto, encargado);
        Bodega bodegaActualizada = bodegaRepository.save(bodega);

        registrarAuditoria("Bodega", OperacionAuditoria.UPDATE, encargado, valorAnterior, construirResumen(bodegaActualizada));
        return bodegaMapper.entidadADTO(bodegaActualizada);
    }

    @Override
    public void eliminarBodega(Long id) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicha bodega a eliminar"));

        String valorAnterior = construirResumen(bodega);
        Usuario usuario = bodega.getEncargado();
        bodegaRepository.delete(bodega);
        registrarAuditoria("Bodega", OperacionAuditoria.DELETE, usuario, valorAnterior, null);
    }

    @Override
    public List<BodegaResponseDTO> listarBodega() {
        return bodegaRepository.findAll().stream()
                .map(bodegaMapper::entidadADTO)
                .toList();
    }

    @Override
    public BodegaResponseDTO buscarPorId(Long id) {
        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicha bodega"));
        return bodegaMapper.entidadADTO(bodega);
    }

    private void registrarAuditoria(String entidad, OperacionAuditoria operacion, Usuario usuario, String valorAnterior, String valorNuevo) {
        Auditoria auditoria = new Auditoria();
        auditoria.setEntidad(entidad);
        auditoria.setOperacion(operacion);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setUsuario(usuario);
        auditoria.setValorAnterior(valorAnterior);
        auditoria.setValorNuevo(valorNuevo);
        auditoriaRepository.save(auditoria);
    }

    private String construirResumen(Bodega bodega) {
        return "id=" + bodega.getId()
                + ", nombre=" + bodega.getNombre()
                + ", ubicacion=" + bodega.getUbicacion()
                + ", capacidad=" + bodega.getCapacidad()
                + ", encargado=" + (bodega.getEncargado() != null ? bodega.getEncargado().getNombre() : "Sin encargado");
    }
}
