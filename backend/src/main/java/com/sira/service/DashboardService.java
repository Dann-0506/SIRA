package com.sira.service;

import com.sira.dto.AlumnoAlertaDto;
import com.sira.dto.DashboardResponse;
import com.sira.dto.GrupoAlertaDto;
import com.sira.model.PeriodoEscolar;
import com.sira.repository.AlumnoRepository;
import com.sira.repository.GrupoRepository;
import com.sira.repository.InscripcionRepository;
import com.sira.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DashboardService {

    @Autowired private PeriodoEscolarService periodoService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private GrupoRepository grupoRepository;
    @Autowired private InscripcionRepository inscripcionRepository;
    @Autowired private AlumnoRepository alumnoRepository;

    @Transactional(readOnly = true)
    public DashboardResponse obtenerDashboard() {
        PeriodoEscolar actual = periodoService.obtenerPeriodoVigente();
        String nombrePeriodo = actual.getNombrePeriodo();

        long alumnosActivos = usuarioRepository.countByRolAndActivo("ALUMNO", true);
        long maestrosActivos = usuarioRepository.countByRolAndActivo("MAESTRO", true);
        
        long gruposEnCurso = grupoRepository.countByActivoAndEstadoEvaluacionAndNombrePeriodo(true, "ABIERTO", nombrePeriodo);
        long inscripcionesActivas = inscripcionRepository.countInscripcionesActivasPorNombrePeriodo(nombrePeriodo);

        List<GrupoAlertaDto> sinActividades = grupoRepository.findGruposSinActividades(nombrePeriodo)
                .stream().map(GrupoAlertaDto::from).toList();

        List<GrupoAlertaDto> pendientesCierre = grupoRepository.findGruposPendientesCierre(nombrePeriodo)
                .stream().map(GrupoAlertaDto::from).toList();

        List<AlumnoAlertaDto> sinInscripciones = alumnoRepository.findAlumnosSinInscripcionesEnSemestre(nombrePeriodo)
                .stream().map(AlumnoAlertaDto::from).toList();

        return new DashboardResponse(nombrePeriodo, alumnosActivos, maestrosActivos, gruposEnCurso,
                inscripcionesActivas, sinActividades, pendientesCierre, sinInscripciones);
    }
}
