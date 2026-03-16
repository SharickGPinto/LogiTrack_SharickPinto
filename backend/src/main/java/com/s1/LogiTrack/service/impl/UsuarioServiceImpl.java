package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.UsuarioRequestDTO;
import com.s1.LogiTrack.dto.response.UsuarioResponseDTO;
import com.s1.LogiTrack.exception.BusinessRuleException;
import com.s1.LogiTrack.mapper.UsuarioMapper;
import com.s1.LogiTrack.model.Auditoria;
import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.model.Usuario;
import com.s1.LogiTrack.repository.AuditoriaRepository;
import com.s1.LogiTrack.repository.BodegaRepository;
import com.s1.LogiTrack.repository.MovimientoRepository;
import com.s1.LogiTrack.repository.UsuarioRepository;
import com.s1.LogiTrack.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final AuditoriaRepository auditoriaRepository;
    private final BodegaRepository bodegaRepository;
    private final MovimientoRepository movimientoRepository;

    @Override
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByUsername(dto.username()) != null) {
            throw new BusinessRuleException("Ya existe un usuario con ese username");
        }

        if (usuarioRepository.findByDocumento(dto.documento()) != null) {
            throw new BusinessRuleException("Ya existe un usuario con ese documento");
        }

        Usuario usuario = usuarioMapper.DTOAEntidad(dto);
        Usuario usuarioInsertado = usuarioRepository.save(usuario);

        registrarAuditoria("Usuario", OperacionAuditoria.INSERT, usuarioInsertado, null, construirResumen(usuarioInsertado));
        return usuarioMapper.entidadADTO(usuarioInsertado);
    }

    @Override
    public UsuarioResponseDTO actualizarUsuario(UsuarioRequestDTO dto, String documento) {
        Usuario u = usuarioRepository.findByDocumento(documento);
        if (u == null) {
            throw new EntityNotFoundException("No existe dicho usuario a actualizar");
        }

        Usuario usuarioPorUsername = usuarioRepository.findByUsername(dto.username());
        if (usuarioPorUsername != null && !usuarioPorUsername.getId().equals(u.getId())) {
            throw new BusinessRuleException("Ya existe otro usuario con ese username");
        }

        Usuario usuarioPorDocumento = usuarioRepository.findByDocumento(dto.documento());
        if (usuarioPorDocumento != null && !usuarioPorDocumento.getId().equals(u.getId())) {
            throw new BusinessRuleException("Ya existe otro usuario con ese documento");
        }

        String valorAnterior = construirResumen(u);
        usuarioMapper.actualizarEntidadDesdeDTO(u, dto);
        Usuario usuarioActualizado = usuarioRepository.save(u);

        registrarAuditoria("Usuario", OperacionAuditoria.UPDATE, usuarioActualizado, valorAnterior, construirResumen(usuarioActualizado));
        return usuarioMapper.entidadADTO(usuarioActualizado);
    }

    @Override
    public void eliminarUsuario(String documento) {
        Usuario u = usuarioRepository.findByDocumento(documento);
        if (u == null) {
            throw new EntityNotFoundException("No existe dicho usuario a eliminar");
        }

        if (bodegaRepository.existsByEncargado(u)) {
            throw new BusinessRuleException("No se puede eliminar el usuario porque es encargado de una o mas bodegas");
        }

        if (movimientoRepository.existsByUsuario(u)) {
            throw new BusinessRuleException("No se puede eliminar el usuario porque tiene movimientos registrados");
        }

        if (auditoriaRepository.existsByUsuario(u)) {
            throw new BusinessRuleException("No se puede eliminar el usuario porque tiene auditorías registradas");
        }

        usuarioRepository.delete(u);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::entidadADTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO buscarPorDocumento(String documento) {
        Usuario u = usuarioRepository.findByDocumento(documento);
        if (u == null) {
            throw new EntityNotFoundException("No existe dicho usuario");
        }
        return usuarioMapper.entidadADTO(u);
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

    private String construirResumen(Usuario usuario) {
        return "id=" + usuario.getId()
                + ", nombre=" + usuario.getNombre()
                + ", documento=" + usuario.getDocumento()
                + ", username=" + usuario.getUsername()
                + ", rol=" + usuario.getRol();
    }
}
