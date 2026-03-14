package com.s1.LogiTrack.dto.request;

import com.s1.LogiTrack.model.TipoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MovimientoRequestDTO(

        @NotNull(message = "El tipo de moviminento es obligatorio")
        TipoMovimiento tipoMovimiento,

        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId,

        Long bodegaOrigenId,
        Long bodegaDestinoId,

        @NotEmpty(message = "debe incluir al menos un producto")
        @Valid
        List<MovimientoDetalleRequestDTO> detalles

) {
}
