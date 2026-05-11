package com.sira.service;

import com.sira.model.Alumno;
import com.sira.model.Carrera;
import com.sira.model.Usuario;
import com.sira.repository.AlumnoRepository;
import com.sira.repository.CarreraRepository;
import com.sira.repository.InscripcionRepository;
import com.sira.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sira.dto.BajaMasivaResultado;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AlumnoService {

    @Autowired private AlumnoRepository alumnoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private InscripcionRepository inscripcionRepository;
    @Autowired private CarreraRepository carreraRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Alumno> listarTodos() {
        return alumnoRepository.findAllWithUsuario();
    }

    @Transactional(readOnly = true)
    public Alumno buscarPorId(Integer id) {
        return alumnoRepository.findByIdWithUsuario(id)
                .orElseThrow(() -> new NoSuchElementException("Alumno no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public Alumno buscarPorUsuarioId(Integer usuarioId) {
        return alumnoRepository.findByUsuarioIdWithUsuario(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Perfil de alumno no encontrado para el usuario: " + usuarioId));
    }

    @Transactional(readOnly = true)
    public Alumno buscarPorMatricula(String matricula) {
        return alumnoRepository.findByMatriculaWithUsuario(matricula)
                .orElseThrow(() -> new NoSuchElementException("Alumno no encontrado con matrícula: " + matricula));
    }

    @Transactional(readOnly = true)
    public List<Alumno> buscarPorGrupo(Integer grupoId) {
        return alumnoRepository.findByGrupoId(grupoId);
    }

    @Transactional
    public Alumno crear(String nombre, String apellidoPaterno, String apellidoMaterno,
                         String email, String matricula, String curp,
                         LocalDate fechaNacimiento, Integer carreraId) {
        validarCampos(nombre, apellidoPaterno, email, matricula, fechaNacimiento, carreraId);
        if (alumnoRepository.existsByMatricula(matricula)) {
            throw new IllegalStateException("El número de control '" + matricula + "' ya está registrado.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("El correo electrónico ya está registrado en el sistema.");
        }
        if (curp != null && !curp.isBlank() && alumnoRepository.existsByCurp(curp.trim().toUpperCase())) {
            throw new IllegalStateException("La CURP ya está registrada en el sistema.");
        }
        String matriculaNormalizada = matricula.trim().toUpperCase();
        String passwordTemporal = fechaNacimiento.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        Usuario usuario = new Usuario(nombre.trim(), apellidoPaterno.trim(),
                (apellidoMaterno != null && !apellidoMaterno.isBlank()) ? apellidoMaterno.trim() : null,
                email.trim(), passwordEncoder.encode(passwordTemporal), "alumno", fechaNacimiento);
        usuario.setRequiereCambioPassword(true);
        usuarioRepository.save(usuario);
        Alumno alumno = new Alumno(usuario, matriculaNormalizada);
        alumno.setCurp(curp != null && !curp.isBlank() ? curp.trim().toUpperCase() : null);
        alumno.setCarrera(carreraRepository.findById(carreraId)
                .orElseThrow(() -> new NoSuchElementException("Carrera no encontrada con id: " + carreraId)));
        Alumno saved = alumnoRepository.save(alumno);
        return alumnoRepository.findByIdWithDetails(saved.getId()).orElseThrow();
    }

    @Transactional
    public Alumno actualizar(Integer id, String nombre, String apellidoPaterno, String apellidoMaterno,
                              String email, String matricula, String curp,
                              LocalDate fechaNacimiento, Integer carreraId) {
        Alumno alumno = buscarPorId(id);
        validarCampos(nombre, apellidoPaterno, email, matricula, fechaNacimiento, carreraId);

        if (!alumno.getMatricula().equals(matricula) && alumnoRepository.existsByMatricula(matricula)) {
            throw new IllegalStateException("El número de control '" + matricula + "' ya está registrado.");
        }

        String curpNormalizada = curp != null && !curp.isBlank() ? curp.trim().toUpperCase() : null;
        if (curpNormalizada != null && !curpNormalizada.equals(alumno.getCurp())
                && alumnoRepository.existsByCurp(curpNormalizada)) {
            throw new IllegalStateException("La CURP ya está registrada en el sistema.");
        }

        Usuario usuario = alumno.getUsuario();
        if (!email.equalsIgnoreCase(usuario.getEmail()) && usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("El correo electrónico ya está registrado en el sistema.");
        }

        usuario.setNombre(nombre.trim());
        usuario.setApellidoPaterno(apellidoPaterno.trim());
        usuario.setApellidoMaterno((apellidoMaterno != null && !apellidoMaterno.isBlank()) ? apellidoMaterno.trim() : null);
        usuario.setEmail(email.trim());
        usuario.setFechaNacimiento(fechaNacimiento);
        usuarioRepository.save(usuario);
        alumno.setMatricula(matricula.trim().toUpperCase());
        alumno.setCurp(curpNormalizada);
        alumno.setCarrera(carreraRepository.findById(carreraId)
                .orElseThrow(() -> new NoSuchElementException("Carrera no encontrada con id: " + carreraId)));
        alumnoRepository.save(alumno);
        return alumnoRepository.findByIdWithDetails(alumno.getId()).orElseThrow();
    }

    @Transactional
    public void cambiarEstado(Integer id, boolean activo) {
        Alumno alumno = buscarPorId(id);
        alumno.getUsuario().setActivo(activo);
        usuarioRepository.save(alumno.getUsuario());
    }

    @Transactional
    public void restablecerPassword(Integer id) {
        Alumno alumno = buscarPorId(id);
        String passwordTemporal = alumno.getUsuario().getFechaNacimiento()
                .format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        alumno.getUsuario().setPasswordHash(passwordEncoder.encode(passwordTemporal));
        alumno.getUsuario().setRequiereCambioPassword(true);
        usuarioRepository.save(alumno.getUsuario());
    }

    @Transactional
    public BajaMasivaResultado bajaMasiva(List<String> matriculas) {
        List<String> desactivados = new ArrayList<>();
        List<String> noEncontrados = new ArrayList<>();
        List<String> yaInactivos = new ArrayList<>();

        for (String matricula : matriculas) {
            String normalizada = matricula.trim().toUpperCase();
            if (normalizada.isBlank()) continue;
            Optional<Alumno> opt = alumnoRepository.findByMatriculaWithUsuario(normalizada);
            if (opt.isEmpty()) {
                noEncontrados.add(normalizada);
            } else {
                Alumno alumno = opt.get();
                if (!alumno.getUsuario().isActivo()) {
                    yaInactivos.add(normalizada);
                } else {
                    alumno.getUsuario().setActivo(false);
                    usuarioRepository.save(alumno.getUsuario());
                    desactivados.add(normalizada);
                }
            }
        }

        return new BajaMasivaResultado(desactivados, noEncontrados, yaInactivos);
    }

    @Transactional
    public void eliminar(Integer id) {
        Alumno alumno = buscarPorId(id);
        if (inscripcionRepository.existsByAlumnoId(id)) {
            throw new IllegalStateException("No se puede eliminar: el alumno tiene registros académicos. Usa 'Desactivar'.");
        }
        Integer usuarioId = alumno.getUsuario().getId();
        alumnoRepository.delete(alumno);
        usuarioRepository.deleteById(usuarioId);
    }

    private void validarCampos(String nombre, String apellidoPaterno, String email,
                                String matricula, LocalDate fechaNacimiento, Integer carreraId) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (apellidoPaterno == null || apellidoPaterno.isBlank()) {
            throw new IllegalArgumentException("El apellido paterno es obligatorio.");
        }
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("La matrícula es obligatoria.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        if (carreraId == null) {
            throw new IllegalArgumentException("La carrera es obligatoria.");
        }
    }
}
