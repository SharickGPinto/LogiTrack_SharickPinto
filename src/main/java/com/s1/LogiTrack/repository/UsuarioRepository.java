package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    Long countByNombre(String nombre);

}
