package com.sira.repository;

import com.sira.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Integer> {

    boolean existsByClave(String clave);

    List<Carrera> findAllByOrderByNombreAsc();

    List<Carrera> findByActivaTrue();
}
