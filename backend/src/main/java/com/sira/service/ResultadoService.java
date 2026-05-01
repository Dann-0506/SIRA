package com.sira.service;

import com.sira.model.Resultado;
import com.sira.repository.ResultadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ResultadoService {

    @Autowired private ResultadoRepository resultadoRepository;
    @Autowired private EstadoUnidadService estadoUnidadService;

    @Transactional(readOnly = true)
    public List<Resultado> buscarPorInscripcionYUnidad(Integer inscripcionId, Integer unidadId) {
        if (inscripcionId == null || unidadId == null) return List.of();
        return resultadoRepository.findByInscripcionIdAndUnidadId(inscripcionId, unidadId);
    }

    @Transactional(readOnly = true)
    public List<Resultado> buscarPorInscripcion(Integer inscripcionId) {
        return resultadoRepository.findByInscripcionId(inscripcionId);
    }

    @Transactional
    public void guardarCalificacion(Integer inscripcionId, Integer grupoId, Integer unidadId,
                                    Integer actividadId, BigDecimal nota) {
        if (nota != null && (nota.compareTo(BigDecimal.ZERO) < 0 || nota.compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("La calificación debe estar entre 0 y 100.");
        }
        estadoUnidadService.validarUnidadAbierta(grupoId, unidadId);
        resultadoRepository.upsert(inscripcionId, actividadId, nota);
    }

    @Transactional
    public void guardarLote(Integer grupoId, Integer unidadId, List<Resultado> resultados) {
        if (resultados == null || resultados.isEmpty()) return;

        for (Resultado r : resultados) {
            if (r.getCalificacion() != null &&
                    (r.getCalificacion().compareTo(BigDecimal.ZERO) < 0 ||
                     r.getCalificacion().compareTo(new BigDecimal("100")) > 0)) {
                throw new IllegalArgumentException("Todas las calificaciones deben estar entre 0 y 100.");
            }
        }
        estadoUnidadService.validarUnidadAbierta(grupoId, unidadId);

        for (Resultado r : resultados) {
            resultadoRepository.upsert(
                    r.getInscripcion().getId(),
                    r.getActividadGrupo().getId(),
                    r.getCalificacion()
            );
        }
    }
}
