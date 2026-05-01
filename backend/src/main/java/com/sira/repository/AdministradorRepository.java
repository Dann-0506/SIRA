package com.sira.repository;

import com.sira.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByUsuarioId(Integer usuarioId);

    boolean existsByNumEmpleado(String numEmpleado);

    @Query("SELECT a FROM Administrador a JOIN FETCH a.usuario ORDER BY a.usuario.nombre ASC")
    List<Administrador> findAllWithUsuario();
}
