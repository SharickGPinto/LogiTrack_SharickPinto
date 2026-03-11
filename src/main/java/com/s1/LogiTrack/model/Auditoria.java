package com.s1.LogiTrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String entidad;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperacionAuditoria operacion;
    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @Column(columnDefinition = "TEXT")
    private String valorAnterior;
    @Column(columnDefinition = "TEXT")
    private String valorNuevo;
}
