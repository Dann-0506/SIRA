package com.sira.repository;

import com.sira.model.PeriodoEscolar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodoEscolarRepository extends JpaRepository<PeriodoEscolar, Integer> {
    
    /**
     * Busca el periodo que está marcado como vigente en el sistema.
     */
    Optional<PeriodoEscolar> findByEsPeriodoActualTrue();
    
    Optional<PeriodoEscolar> findByNombrePeriodo(String nombrePeriodo);
}
