package com.sira.dto;

public record MateriaReprobacionDto(
        Integer materiaId,
        String clave,
        String nombre,
        long gruposEvaluados,
        long totalAlumnos,
        long aprobados,
        long reprobados
) {
    public double porcentajeReprobacion() {
        if (totalAlumnos == 0) return 0;
        return Math.round((reprobados * 100.0 / totalAlumnos) * 10.0) / 10.0;
    }
}
