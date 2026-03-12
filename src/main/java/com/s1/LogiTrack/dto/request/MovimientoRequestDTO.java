package com.s1.LogiTrack.dto.request;

import com.s1.LogiTrack.dto.response.MovimientoDetalleResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MovimientoRequestDTO(

        @NotNull(message = "El tipo de moviminento es obligatorio")
        String productoId,

        Long bodegaOrigenId,
        Long bodegaDestinoID,

        @NotEmpty(message =  "debe incluir al menos un producto")
        @Valid
        List<MovimientoDetalleResponseDTO> detalles


) {
}
