package com.s1.LogiTrack.dto.request;

import com.s1.LogiTrack.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @Schema(description = "Se debe ingresarr el nombre de la persona",
                example = "Juan")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
        String nombre,

        @Schema(description = "Se debe ingresar el documento de la persona",
                example = "10987657")
        @NotBlank(message = "El documento no puede estar vacío")
        @Size(min = 5, max = 20, message = "El documento debe tener entre 5 y 20 caracteres")
        String documento,

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotNull(message = "El rol no puede ser nulo")
        Rol rol,

        @NotBlank(message = "El username no puede estar vacío")
        String username
) {
}
