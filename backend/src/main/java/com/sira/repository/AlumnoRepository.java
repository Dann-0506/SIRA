package com.sira.repository;

import com.sira.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {

    Optional<Alumno> findByMatricula(String matricula);

    Optional<Alumno> findByUsuarioId(Integer usuarioId);

    boolean existsByMatricula(String matricula);

    @Query("SELECT a FROM Alumno a JOIN FETCH a.usuario ORDER BY a.usuario.nombre ASC")
    List<Alumno> findAllWithUsuario();

    @Query("""
        SELECT a FROM Alumno a JOIN FETCH a.usuario
        JOIN Inscripcion i ON i.alumno = a
        WHERE i.grupo.id = :grupoId
        ORDER BY a.usuario.nombre ASC
        """)
    List<Alumno> findByGrupoId(Integer grupoId);
}
