package com.sira.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un ciclo o periodo académico dentro de la institución.
 * Centraliza las fechas de vigencia y los criterios de evaluación globales para dicho periodo.
 */
@Entity
@Table(name = "periodo_escolar")
@Getter @Setter @NoArgsConstructor
public class PeriodoEscolar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", length = 100, nullable = false, unique = true)
    private String nombrePeriodo; // Ej: "Enero - Junio 2026"

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicioPeriodo;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFinPeriodo;

    @Column(name = "minima_aprobatoria", precision = 5, scale = 2, nullable = false)
    private BigDecimal calificacionMinimaAprobatoria = new BigDecimal("70.00");

    @Column(name = "maxima", precision = 5, scale = 2, nullable = false)
    private BigDecimal calificacionMaximaPosible = new BigDecimal("100.00");

    @Column(name = "actual", nullable = false)
    private boolean esPeriodoActual = false;

    public PeriodoEscolar(String nombrePeriodo, LocalDate fechaInicioPeriodo, LocalDate fechaFinPeriodo) {
        this.nombrePeriodo = nombrePeriodo;
        this.fechaInicioPeriodo = fechaInicioPeriodo;
        this.fechaFinPeriodo = fechaFinPeriodo;
    }
}
