package com.sira.dto;

import com.sira.model.Administrador;

import java.time.LocalDate;

public record AdminResponse(
        Integer id,
        String numEmpleado,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        boolean activo,
        LocalDate fechaNacimiento
) {
    public static AdminResponse from(Administrador a) {
        return new AdminResponse(
                a.getId(),
                a.getNumEmpleado(),
                a.getUsuario().getNombre(),
                a.getUsuario().getApellidoPaterno(),
                a.getUsuario().getApellidoMaterno(),
                a.getUsuario().getEmail(),
                a.getUsuario().isActivo(),
                a.getUsuario().getFechaNacimiento()
        );
    }
}
