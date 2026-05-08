package com.sira.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "alumno", uniqueConstraints = {
    @UniqueConstraint(columnNames = "usuario_id"),
    @UniqueConstraint(columnNames = "matricula")
})
@Getter @Setter @NoArgsConstructor
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "matricula", length = 20, nullable = false, unique = true)
    private String matricula;

    @Column(name = "curp", length = 18, unique = true)
    private String curp;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    public Alumno(Usuario usuario, String matricula) {
        this.usuario = usuario;
        this.matricula = matricula;
    }

    @Override
    public String toString() {
        return "[" + matricula + "] " + (usuario != null ? usuario.getNombreCompleto() : "");
    }
}
