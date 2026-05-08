package com.sira.service;

import com.sira.model.Carrera;
import com.sira.repository.AlumnoRepository;
import com.sira.repository.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarreraService {

    @Autowired private CarreraRepository carreraRepository;
    @Autowired private AlumnoRepository alumnoRepository;

    @Transactional(readOnly = true)
    public List<Carrera> listarTodas() {
        return carreraRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Carrera> listarActivas() {
        return carreraRepository.findByActivaTrue();
    }

    @Transactional(readOnly = true)
    public Carrera buscarPorId(Integer id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Carrera no encontrada con id: " + id));
    }

    @Transactional
    public Carrera crear(String clave, String nombre) {
        validarCampos(clave, nombre);
        if (carreraRepository.existsByClave(clave.trim().toUpperCase())) {
            throw new IllegalStateException("La clave '" + clave + "' ya está registrada.");
        }
        return carreraRepository.save(new Carrera(clave.trim().toUpperCase(), nombre.trim()));
    }

    @Transactional
    public Carrera actualizar(Integer id, String clave, String nombre) {
        Carrera carrera = buscarPorId(id);
        validarCampos(clave, nombre);
        String claveNormalizada = clave.trim().toUpperCase();
        if (!carrera.getClave().equals(claveNormalizada) && carreraRepository.existsByClave(claveNormalizada)) {
            throw new IllegalStateException("La clave '" + clave + "' ya está registrada.");
        }
        carrera.setClave(claveNormalizada);
        carrera.setNombre(nombre.trim());
        return carreraRepository.save(carrera);
    }

    @Transactional
    public void cambiarEstado(Integer id, boolean activa) {
        Carrera carrera = buscarPorId(id);
        carrera.setActiva(activa);
        carreraRepository.save(carrera);
    }

    @Transactional
    public void eliminar(Integer id) {
        Carrera carrera = buscarPorId(id);
        if (alumnoRepository.existsByCarreraId(id)) {
            throw new IllegalStateException("No se puede eliminar: hay alumnos inscritos en esta carrera. Usa 'Desactivar'.");
        }
        carreraRepository.delete(carrera);
    }

    private void validarCampos(String clave, String nombre) {
        if (clave == null || clave.isBlank()) {
            throw new IllegalArgumentException("La clave es obligatoria.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
    }
}
