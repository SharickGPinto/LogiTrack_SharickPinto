package com.s1.LogiTrack.dto.response;

import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.model.Usuario;

import java.time.LocalDateTime;

public record AuditoriaResponseDTO(
 Long id, String entidad, OperacionAuditoria operacion,
 LocalDateTime fecha, Usuario nombreUsuario, String valorAnterior,
 String valorNuevo
) {
}
