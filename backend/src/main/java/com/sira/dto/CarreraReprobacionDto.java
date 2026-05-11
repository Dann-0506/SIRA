package com.sira.dto;

public record CarreraReprobacionDto(
        Integer carreraId,
        String clave,
        String nombre,
        long totalCursadas,
        long reprobados,
        double porcentajeReprobacion
) {}
