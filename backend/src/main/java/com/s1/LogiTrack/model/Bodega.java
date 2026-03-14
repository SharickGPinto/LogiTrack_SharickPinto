package com.s1.LogiTrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bodegas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String ubicacion;
    @Column (nullable = false)
    private Integer capacidad;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargado_id", nullable = false)
    private Usuario encargado;
}
