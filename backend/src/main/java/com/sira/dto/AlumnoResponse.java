package com.sira.dto;

import com.sira.model.Alumno;

import java.time.LocalDate;

public record AlumnoResponse(
        Integer id,
        Integer usuarioId,
        String numControl,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        boolean activo,
        String curp,
        LocalDate fechaNacimiento,   // siempre presente
        Integer carreraId,           // siempre presente
        String carreraNombre         // siempre presente
) {
    public static AlumnoResponse from(Alumno a) {
        return new AlumnoResponse(
                a.getId(),
                a.getUsuario().getId(),
                a.getMatricula(),
                a.getUsuario().getNombre(),
                a.getUsuario().getApellidoPaterno(),
                a.getUsuario().getApellidoMaterno(),
                a.getUsuario().getEmail(),
                a.getUsuario().isActivo(),
                a.getCurp(),
                a.getUsuario().getFechaNacimiento(),
                a.getCarrera() != null ? a.getCarrera().getId() : null,
                a.getCarrera() != null ? a.getCarrera().getNombre() : null
        );
    }
}
