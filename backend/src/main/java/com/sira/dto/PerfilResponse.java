package com.sira.dto;

import com.sira.model.Usuario;

import java.time.LocalDate;

public record PerfilResponse(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String rol,
        String identificador,
        LocalDate fechaNacimiento
) {
    public static PerfilResponse of(Usuario usuario, String identificador) {
        return new PerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidoPaterno(),
                usuario.getApellidoMaterno(),
                usuario.getEmail(),
                usuario.getRol(),
                identificador,
                usuario.getFechaNacimiento()
        );
    }
}
