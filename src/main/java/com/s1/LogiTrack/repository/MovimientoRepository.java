package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.Movimiento;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, String> {
    boolean existsByUsuario(Usuario usuario);
}
