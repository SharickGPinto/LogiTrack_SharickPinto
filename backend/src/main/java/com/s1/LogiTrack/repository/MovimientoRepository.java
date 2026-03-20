package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.Movimiento;
import com.s1.LogiTrack.model.Producto;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    boolean existsByUsuario(Usuario usuario);
    List<Movimiento> findBylistarRecientes(Integer movimiento);
}
