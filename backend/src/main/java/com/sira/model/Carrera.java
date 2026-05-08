package com.sira.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carrera", uniqueConstraints = {
    @UniqueConstraint(columnNames = "clave")
})
@Getter @Setter @NoArgsConstructor
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clave", length = 20, nullable = false, unique = true)
    private String clave;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    public Carrera(String clave, String nombre) {
        this.clave = clave;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre + " (" + clave + ")";
    }
}
