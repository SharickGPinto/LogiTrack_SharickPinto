package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.request.BodegaRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public BodegaResponseDTO entidadADTO(Bodega bodega) {
        if (bodega == null) return null;

        return new BodegaResponseDTO(
                bodega.getId(),
                bodega.getNombre(),
                bodega.getUbicacion(),
                bodega.getCapacidad(),
                bodega.getEncargado() != null ? bodega.getEncargado().getNombre() : "Sin encargado"
        );
    }

    public Bodega DTOAEntidad(BodegaRequestDTO dto, Usuario encargado) {
        if (dto == null || encargado == null) return null;

        Bodega b = new Bodega();
        b.setNombre(dto.nombre());
        b.setUbicacion(dto.ubicacion());
        b.setCapacidad(dto.capacidad());
        b.setEncargado(encargado);
        return b;
    }

    public void actualizarEntidadDesdeDTO(Bodega b, BodegaRequestDTO dto, Usuario encargado) {
        if (dto == null || encargado == null) return;

        b.setNombre(dto.nombre());
        b.setUbicacion(dto.ubicacion());
        b.setCapacidad(dto.capacidad());
        b.setEncargado(encargado);
    }
}
