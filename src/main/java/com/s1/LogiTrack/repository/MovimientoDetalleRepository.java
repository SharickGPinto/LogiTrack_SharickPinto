package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.MovimientoDetalle;
import com.s1.LogiTrack.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoDetalleRepository extends JpaRepository<MovimientoDetalle, Long> {
    boolean existsByProducto(Producto producto);
}
