package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {
    List<Bodega> findByNombreIgnoreCase(String nombre);
    boolean existsByNombre(String nombre);
    Long countByCapacidad(Integer capacidad);
    boolean existsByEncargado(Usuario encargado);

}
