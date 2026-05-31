package com.sira.dto;

import com.sira.model.Grupo;

import java.math.BigDecimal;

public record GrupoResponse(
        Integer id,
        String clave,
        Integer periodoId,
        String semestre, // Mantenemos el nombre por compatibilidad con el frontend
        Integer materiaId,
        String materiaNombre,
        String materiaClave,
        Integer maestroId,
        String maestroNombre,
        boolean activo,
        String estadoEvaluacion,
        BigDecimal calificacionMinimaAprobatoria,
        BigDecimal calificacionMaxima
) {
    public static GrupoResponse from(Grupo g) {
        return new GrupoResponse(
                g.getId(),
                g.getClave(),
                g.getPeriodo().getId(),
                g.getSemestre(),
                g.getMateria().getId(),
                g.getMateria().getNombre(),
                g.getMateria().getClave(),
                g.getMaestro().getId(),
                g.getMaestro().getUsuario().getNombreCompleto(),
                g.isActivo(),
                g.getEstadoEvaluacion(),
                g.getCalificacionMinimaAprobatoria(),
                g.getCalificacionMaxima()
        );
    }
}
