package com.sira.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter @Setter @NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "apellido_paterno", length = 80, nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 80)
    private String apellidoMaterno;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "rol", length = 20, nullable = false)
    private String rol;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "requiere_cambio_password", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean requiereCambioPassword = false;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    public Usuario(String nombre, String apellidoPaterno, String apellidoMaterno,
                   String email, String passwordHash, String rol, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder(nombre);
        if (apellidoPaterno != null && !apellidoPaterno.isBlank()) sb.append(" ").append(apellidoPaterno);
        if (apellidoMaterno != null && !apellidoMaterno.isBlank()) sb.append(" ").append(apellidoMaterno);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + rol + ")";
    }
}
