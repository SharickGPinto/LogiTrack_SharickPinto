package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.response.AuditoriaResponseDTO;
import com.s1.LogiTrack.model.Auditoria;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public AuditoriaResponseDTO entidadADTO(Auditoria auditoria){
        if (auditoria == null) return null;

        return new AuditoriaResponseDTO(
                auditoria.getId(),
                auditoria.getEntidad(),
                auditoria.getOperacion(),
                auditoria.getFecha(),
                auditoria.getUsuario() != null ? auditoria.getUsuario().getNombre() : null,
                auditoria.getValorAnterior(),
                auditoria.getValorNuevo()
        );
    }
}
