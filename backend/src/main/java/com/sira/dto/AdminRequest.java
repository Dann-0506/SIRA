package com.sira.dto;

public record AdminRequest(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String numEmpleado
) {}
