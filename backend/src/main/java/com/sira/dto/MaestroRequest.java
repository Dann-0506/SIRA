package com.sira.dto;

public record MaestroRequest(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String numEmpleado
) {}
