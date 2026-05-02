package com.sira.dto;

public record MaestroAprovechamientoDto(
        Integer maestroId,
        String nombre,
        String numEmpleado,
        long grupos,
        long alumnosEvaluados,
        long aprobados,
        long reprobados
) {
    public double porcentajeAprobacion() {
        if (alumnosEvaluados == 0) return 0;
        return Math.round((aprobados * 100.0 / alumnosEvaluados) * 10.0) / 10.0;
    }
}
