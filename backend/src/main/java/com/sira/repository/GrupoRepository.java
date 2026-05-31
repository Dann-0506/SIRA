package com.sira.repository;

import com.sira.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {

    @Query("""
        SELECT g FROM Grupo g
        JOIN FETCH g.materia
        JOIN FETCH g.maestro m
        JOIN FETCH m.usuario
        JOIN FETCH g.periodo
        WHERE g.clave = :clave AND g.periodo.nombrePeriodo = :nombrePeriodo
        """)
    Optional<Grupo> findByClaveAndNombrePeriodo(@Param("clave") String clave, @Param("nombrePeriodo") String nombrePeriodo);

    @Query("""
        SELECT g FROM Grupo g
        JOIN FETCH g.materia
        JOIN FETCH g.maestro m
        JOIN FETCH m.usuario
        JOIN FETCH g.periodo
        ORDER BY g.periodo.fechaInicioPeriodo DESC, g.materia.nombre ASC
        """)
    List<Grupo> findAllWithDetails();

    @Query("""
        SELECT g FROM Grupo g
        JOIN FETCH g.materia
        JOIN FETCH g.maestro m
        JOIN FETCH m.usuario
        JOIN FETCH g.periodo
        WHERE m.id = :maestroId AND g.activo = true
        ORDER BY g.periodo.fechaInicioPeriodo DESC, g.materia.nombre ASC
        """)
    List<Grupo> findByMaestroIdAbiertos(Integer maestroId);

    @Query("""
        SELECT g FROM Grupo g
        JOIN FETCH g.materia
        JOIN FETCH g.maestro m
        JOIN FETCH m.usuario
        JOIN FETCH g.periodo
        JOIN Inscripcion i ON i.grupo = g
        WHERE i.alumno.id = :alumnoId
        ORDER BY g.periodo.fechaInicioPeriodo DESC, g.materia.nombre ASC
        """)
    List<Grupo> findByAlumnoId(Integer alumnoId);

    @Query("""
        SELECT g FROM Grupo g
        JOIN FETCH g.materia
        JOIN FETCH g.maestro m
        JOIN FETCH m.usuario
        JOIN FETCH g.periodo
        WHERE g.id = :id
        """)
    Optional<Grupo> findByIdWithDetails(Integer id);

    @Query("SELECT COUNT(g) FROM Grupo g WHERE g.activo = :activo AND g.estadoEvaluacion = :estadoEvaluacion AND g.periodo.nombrePeriodo = :nombrePeriodo")
    long countByActivoAndEstadoEvaluacionAndNombrePeriodo(@Param("activo") boolean activo, @Param("estadoEvaluacion") String estadoEvaluacion, @Param("nombrePeriodo") String nombrePeriodo);

    @Query("""
        SELECT g FROM Grupo g
        JOIN FETCH g.materia
        JOIN FETCH g.maestro m
        JOIN FETCH m.usuario
        JOIN FETCH g.periodo
        WHERE g.estadoEvaluacion = 'ABIERTO' AND g.activo = true AND g.periodo.nombrePeriodo = :nombrePeriodo
        AND NOT EXISTS (SELECT a FROM ActividadGrupo a WHERE a.grupo = g)
        ORDER BY g.materia.nombre ASC
        """)
    List<Grupo> findGruposSinActividades(@Param("nombrePeriodo") String nombrePeriodo);

    @Query("""
        SELECT g FROM Grupo g
        JOIN FETCH g.materia
        JOIN FETCH g.maestro m
        JOIN FETCH m.usuario
        JOIN FETCH g.periodo
        WHERE g.estadoEvaluacion = 'ABIERTO' AND g.activo = true AND g.periodo.nombrePeriodo = :nombrePeriodo
        AND g.materia.totalUnidades > 0
        AND (SELECT COUNT(eu) FROM EstadoUnidad eu WHERE eu.grupo = g AND eu.estado = 'CERRADA')
            = g.materia.totalUnidades
        ORDER BY g.materia.nombre ASC
        """)
    List<Grupo> findGruposPendientesCierre(@Param("nombrePeriodo") String nombrePeriodo);

    @Query("SELECT g.periodo.nombrePeriodo FROM Grupo g GROUP BY g.periodo.nombrePeriodo, g.periodo.fechaInicioPeriodo ORDER BY g.periodo.fechaInicioPeriodo DESC")
    List<String> findSemestresDisponibles();

    @Query("SELECT (COUNT(g) > 0) FROM Grupo g WHERE g.clave = :clave AND g.materia.id = :materiaId AND g.periodo.id = :periodoId")
    boolean existsByClaveAndMateriaIdAndPeriodoId(String clave, Integer materiaId, Integer periodoId);

    boolean existsByMaestroId(Integer maestroId);
}
