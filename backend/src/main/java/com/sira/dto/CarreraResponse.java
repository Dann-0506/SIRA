package com.sira.dto;

import com.sira.model.Carrera;

public record CarreraResponse(Integer id, String clave, String nombre, boolean activa) {

    public static CarreraResponse from(Carrera c) {
        return new CarreraResponse(c.getId(), c.getClave(), c.getNombre(), c.isActiva());
    }
}
