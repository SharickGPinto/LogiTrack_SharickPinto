package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.Auditoria;
import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuarioIgnoreCase(String usuario);
    List<Auditoria> findByOperacion(OperacionAuditoria operacion);


}
