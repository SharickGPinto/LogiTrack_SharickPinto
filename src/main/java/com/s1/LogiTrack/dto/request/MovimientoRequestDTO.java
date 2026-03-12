package com.s1.LogiTrack.dto.request;

import com.s1.LogiTrack.dto.response.MovimientoDetalleResponseDTO;
import com.s1.LogiTrack.model.TipoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MovimientoRequestDTO(

        @NotNull(message = "El tipo de moviminento es obligatorio")
        TipoMovimiento tipoMovimiento,

        Long bodegaOrigenId,
        Long bodegaDestinoID,

        @NotEmpty(message =  "debe incluir al menos un producto")
        @Valid
        List<MovimientoDetalleResponseDTO> detalles


) {
}
