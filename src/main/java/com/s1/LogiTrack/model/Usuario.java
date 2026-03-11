package com.s1.LogiTrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (nullable = false)
    private String nombre;
    @Column (nullable = false, unique = true)
    private String documento;
    @Column (nullable = false, unique = true)
    private String username;
    @Column (nullable = false)
    private  String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

}
