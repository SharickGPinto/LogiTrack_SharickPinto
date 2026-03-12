package com.s1.LogiTrack.dto.response;

import com.s1.LogiTrack.model.Rol;

public record UsuarioresponseDTO(
        Long id, String nombre, String documento, String username, Rol rol
) {
}
