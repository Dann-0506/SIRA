package com.sira.repository;

import com.sira.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    Optional<Materia> findByClave(String clave);

    boolean existsByClave(String clave);

    List<Materia> findAllByOrderByNombreAsc();
}
