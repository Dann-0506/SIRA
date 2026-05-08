package com.sira.dto;

import com.sira.model.Usuario;

public record PerfilResponse(
        Integer id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String rol,
        String identificador
) {
    public static PerfilResponse of(Usuario usuario, String identificador) {
        return new PerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidoPaterno(),
                usuario.getApellidoMaterno(),
                usuario.getEmail(),
                usuario.getRol(),
                identificador
        );
    }
}
