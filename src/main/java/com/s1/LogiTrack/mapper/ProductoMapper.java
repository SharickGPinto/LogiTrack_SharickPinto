package com.s1.LogiTrack.mapper;

import com.s1.LogiTrack.dto.request.ProductoRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.dto.response.ProductoResponseDTO;
import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    public ProductoResponseDTO entidadADTO(Producto p, BodegaResponseDTO bodegaDTO) {
        if (p == null || bodegaDTO == null) return null;

        return new ProductoResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock(),
                bodegaDTO // Pasamos el DTO anidado
        );
    }

    public Producto DTOAEntidad(ProductoRequestDTO dto, Bodega bodega) {
        if (dto == null || bodega == null) return null;

        Producto p = new Producto();
        p.setNombre(dto.nombre());
        p.setCategoria(dto.categoria());
        p.setPrecio(dto.precio());
        p.setStock(dto.stock());
        p.setBodega(bodega);
        return p;
    }

    public void actualizarEntidadDesdeDTO(Producto p, ProductoRequestDTO dto, Bodega bodega) {
        if (dto == null || bodega == null) return;

        p.setNombre(dto.nombre());
        p.setCategoria(dto.categoria());
        p.setPrecio(dto.precio());
        p.setStock(dto.stock());
        p.setBodega(bodega);
    }
}
