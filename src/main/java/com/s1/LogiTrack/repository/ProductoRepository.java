package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreIgnoreCase(String nombre);
    boolean existsByNombre(String nombre);
    Long countByNombre(String nombre);
}
