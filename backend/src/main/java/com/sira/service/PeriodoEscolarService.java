package com.sira.service;

import com.sira.model.PeriodoEscolar;
import com.sira.repository.PeriodoEscolarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PeriodoEscolarService {

    @Autowired private PeriodoEscolarRepository periodoRepository;

    @Transactional(readOnly = true)
    public PeriodoEscolar obtenerPeriodoVigente() {
        return periodoRepository.findByEsPeriodoActualTrue()
                .orElseThrow(() -> new RuntimeException("No hay un periodo escolar marcado como actual/vigente."));
    }

    @Transactional(readOnly = true)
    public List<PeriodoEscolar> listarTodos() {
        return periodoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PeriodoEscolar obtenerPorId(Integer id) {
        return periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Periodo no encontrado."));
    }

    @Transactional
    public PeriodoEscolar guardar(PeriodoEscolar periodo) {
        validarPeriodo(periodo);
        
        // Si se marca como actual, debemos desactivar cualquier otro periodo previo
        if (periodo.isEsPeriodoActual()) {
            periodoRepository.findByEsPeriodoActualTrue().ifPresent(p -> {
                if (!p.getId().equals(periodo.getId())) {
                    p.setEsPeriodoActual(false);
                    periodoRepository.save(p);
                }
            });
        }
        
        return periodoRepository.save(periodo);
    }

    @Transactional
    public void marcarComoActual(Integer id) {
        PeriodoEscolar periodo = obtenerPorId(id);
        periodo.setEsPeriodoActual(true);
        guardar(periodo);
    }

    @Transactional
    public void eliminar(Integer id) {
        PeriodoEscolar periodo = obtenerPorId(id);
        if (periodo.isEsPeriodoActual()) {
            throw new RuntimeException("No se puede eliminar el periodo actual.");
        }
        periodoRepository.delete(periodo);
    }

    private void validarPeriodo(PeriodoEscolar p) {
        if (p.getFechaInicioPeriodo().isAfter(p.getFechaFinPeriodo())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        if (p.getCalificacionMinimaAprobatoria().compareTo(BigDecimal.ZERO) < 0 || 
            p.getCalificacionMaximaPosible().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Las calificaciones deben ser positivas.");
        }
        if (p.getCalificacionMinimaAprobatoria().compareTo(p.getCalificacionMaximaPosible()) >= 0) {
            throw new IllegalArgumentException("La calificación mínima debe ser menor a la máxima.");
        }
    }

    // --- Métodos de compatibilidad con ConfiguracionService ---

    @Transactional(readOnly = true)
    public BigDecimal obtenerCalificacionMinimaDefault() {
        return periodoRepository.findByEsPeriodoActualTrue()
                .map(PeriodoEscolar::getCalificacionMinimaAprobatoria)
                .orElse(new BigDecimal("70"));
    }

    @Transactional(readOnly = true)
    public BigDecimal obtenerCalificacionMaximaDefault() {
        return periodoRepository.findByEsPeriodoActualTrue()
                .map(PeriodoEscolar::getCalificacionMaximaPosible)
                .orElse(new BigDecimal("100"));
    }

    @Transactional(readOnly = true)
    public String obtenerNombrePeriodoActual() {
        return periodoRepository.findByEsPeriodoActualTrue()
                .map(PeriodoEscolar::getNombrePeriodo)
                .orElse("");
    }
}
