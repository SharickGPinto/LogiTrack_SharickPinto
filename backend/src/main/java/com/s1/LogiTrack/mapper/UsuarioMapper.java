package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.request.UsuarioRequestDTO;
import com.s1.LogiTrack.dto.response.UsuarioResponseDTO;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO entidadADTO(Usuario usuario) {
        if (usuario == null) return null;

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getDocumento(),
                usuario.getUsername(),
                usuario.getRol()
        );
    }

    public Usuario DTOAEntidad(UsuarioRequestDTO dto) {
        if (dto == null) return null;
        Usuario u = new Usuario();
        u.setNombre(dto.nombre());
        u.setDocumento(dto.documento());
        u.setUsername(dto.username());
        u.setPassword(dto.password());
        u.setRol(dto.rol());

        return u;
    }

    public void actualizarEntidadDesdeDTO(Usuario u, UsuarioRequestDTO dto) {
        if (dto == null || u == null) return;
        u.setNombre(dto.nombre());
        u.setDocumento(dto.documento());
        u.setUsername(dto.username());
        u.setPassword(dto.password());
        u.setRol(dto.rol());
    }
}
