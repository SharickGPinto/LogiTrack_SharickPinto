package com.s1.LogiTrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioRequestDTO(
        @Schema(description = "Se debe ingresarr el nombre de la persona",
                example = "Juan")
        String nombre,
        @Schema(description = "Se debe ingresar el documento de la persona",
        example = "10987657")
        String documento
) {
}
