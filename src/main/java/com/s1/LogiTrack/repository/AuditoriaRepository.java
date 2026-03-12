package com.s1.LogiTrack.repository;

import com.s1.LogiTrack.model.Auditoria;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByIdIgnoreCase(Long id);


}
