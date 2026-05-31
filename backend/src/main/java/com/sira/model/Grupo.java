package com.sira.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "grupo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"clave", "materia_id", "periodo_id"})
})
@Getter @Setter @NoArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maestro_id", nullable = false)
    private Maestro maestro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_id", nullable = false)
    private PeriodoEscolar periodo;

    @Column(name = "clave", length = 20, nullable = false)
    private String clave;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "estado_evaluacion", length = 20, nullable = false)
    private String estadoEvaluacion = "ABIERTO";

    public Grupo(Materia materia, Maestro maestro, String clave, PeriodoEscolar periodo) {
        this.materia = materia;
        this.maestro = maestro;
        this.clave = clave;
        this.periodo = periodo;
    }

    /**
     * Devuelve el nombre del periodo al que pertenece este grupo.
     * Mantenido por compatibilidad con lógica que espera el String del semestre.
     */
    public String getSemestre() {
        return periodo != null ? periodo.getNombrePeriodo() : null;
    }

    /**
     * Obtiene la calificación mínima aprobatoria configurada para el periodo de este grupo.
     */
    public BigDecimal getCalificacionMinimaAprobatoria() {
        return periodo != null ? periodo.getCalificacionMinimaAprobatoria() : new BigDecimal("70.00");
    }

    /**
     * Obtiene la calificación máxima posible configurada para el periodo de este grupo.
     */
    public BigDecimal getCalificacionMaxima() {
        return periodo != null ? periodo.getCalificacionMaximaPosible() : new BigDecimal("100.00");
    }

    public boolean isCerrado() {
        return "CERRADO".equals(estadoEvaluacion);
    }

    @Override
    public String toString() {
        return "[" + clave + "] " + (materia != null ? materia.getNombre() : "") + " (" + getSemestre() + ")";
    }
}
