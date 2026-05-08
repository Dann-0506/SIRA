package com.sira.service;

import com.sira.model.Administrador;
import com.sira.model.Usuario;
import com.sira.repository.AdministradorRepository;
import com.sira.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminService {

    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Administrador> listarTodos() {
        return administradorRepository.findAllWithUsuario();
    }

    @Transactional(readOnly = true)
    public Administrador buscarPorId(Integer id) {
        return administradorRepository.findByIdWithUsuario(id)
                .orElseThrow(() -> new NoSuchElementException("Administrador no encontrado con id: " + id));
    }

    @Transactional
    public Administrador crear(String nombre, String apellidoPaterno, String apellidoMaterno,
                                String email, String numEmpleado, LocalDate fechaNacimiento) {
        validarCampos(nombre, apellidoPaterno, email, numEmpleado, fechaNacimiento);
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("El correo electrónico ya está registrado en el sistema.");
        }
        if (administradorRepository.existsByNumEmpleado(numEmpleado)) {
            throw new IllegalStateException("El número de empleado '" + numEmpleado + "' ya está registrado.");
        }
        String numEmpleadoNormalizado = numEmpleado.trim().toUpperCase();
        String passwordTemporal = fechaNacimiento.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        Usuario usuario = new Usuario(nombre.trim(), apellidoPaterno.trim(),
                (apellidoMaterno != null && !apellidoMaterno.isBlank()) ? apellidoMaterno.trim() : null,
                email.trim().toLowerCase(), passwordEncoder.encode(passwordTemporal), "admin", fechaNacimiento);
        usuario.setRequiereCambioPassword(true);
        usuarioRepository.save(usuario);
        Administrador saved = administradorRepository.save(new Administrador(usuario, numEmpleadoNormalizado));
        return administradorRepository.findByIdWithUsuario(saved.getId()).orElseThrow();
    }

    @Transactional
    public Administrador actualizar(Integer id, String nombre, String apellidoPaterno, String apellidoMaterno,
                                     String email, String numEmpleado, LocalDate fechaNacimiento, Integer actorId) {
        Administrador admin = buscarPorId(id);
        verificarNoEsMismoUsuario(admin, actorId);
        validarCampos(nombre, apellidoPaterno, email, numEmpleado, fechaNacimiento);

        if (!admin.getUsuario().getEmail().equalsIgnoreCase(email) && usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("El correo electrónico ya está registrado en el sistema.");
        }
        if (!admin.getNumEmpleado().equals(numEmpleado) && administradorRepository.existsByNumEmpleado(numEmpleado)) {
            throw new IllegalStateException("El número de empleado '" + numEmpleado + "' ya está registrado.");
        }

        admin.getUsuario().setNombre(nombre.trim());
        admin.getUsuario().setApellidoPaterno(apellidoPaterno.trim());
        admin.getUsuario().setApellidoMaterno((apellidoMaterno != null && !apellidoMaterno.isBlank()) ? apellidoMaterno.trim() : null);
        admin.getUsuario().setEmail(email.trim().toLowerCase());
        admin.getUsuario().setFechaNacimiento(fechaNacimiento);
        usuarioRepository.save(admin.getUsuario());
        admin.setNumEmpleado(numEmpleado.trim().toUpperCase());
        administradorRepository.save(admin);
        return administradorRepository.findByIdWithUsuario(id).orElseThrow();
    }

    @Transactional
    public void cambiarEstado(Integer id, boolean activo, Integer actorId) {
        Administrador admin = buscarPorId(id);
        verificarNoEsMismoUsuario(admin, actorId);
        if (!activo) {
            verificarNoEsUltimoAdminActivo(admin);
        }
        admin.getUsuario().setActivo(activo);
        usuarioRepository.save(admin.getUsuario());
    }

    @Transactional
    public void restablecerPassword(Integer id, Integer actorId) {
        Administrador admin = buscarPorId(id);
        verificarNoEsMismoUsuario(admin, actorId);
        String passwordTemporal = admin.getUsuario().getFechaNacimiento()
                .format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        admin.getUsuario().setPasswordHash(passwordEncoder.encode(passwordTemporal));
        admin.getUsuario().setRequiereCambioPassword(true);
        usuarioRepository.save(admin.getUsuario());
    }

    @Transactional
    public void eliminar(Integer id, Integer actorId) {
        Administrador admin = buscarPorId(id);
        verificarNoEsMismoUsuario(admin, actorId);
        verificarNoEsUltimoAdminActivo(admin);
        Integer usuarioId = admin.getUsuario().getId();
        administradorRepository.delete(admin);
        usuarioRepository.deleteById(usuarioId);
    }

    // ==========================================
    // GUARDIAS DE SEGURIDAD
    // ==========================================

    private void verificarNoEsMismoUsuario(Administrador target, Integer actorId) {
        if (target.getUsuario().getId().equals(actorId)) {
            throw new IllegalStateException("No puedes realizar esta acción sobre tu propia cuenta.");
        }
    }

    private void verificarNoEsUltimoAdminActivo(Administrador target) {
        long adminsActivos = usuarioRepository.countByRolAndActivo("admin", true);
        if (adminsActivos <= 1 && target.getUsuario().isActivo()) {
            throw new IllegalStateException(
                    "Operación denegada: no puedes eliminar ni desactivar al único administrador activo del sistema.");
        }
    }

    private void validarCampos(String nombre, String apellidoPaterno, String email,
                                String numEmpleado, LocalDate fechaNacimiento) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (apellidoPaterno == null || apellidoPaterno.isBlank()) {
            throw new IllegalArgumentException("El apellido paterno es obligatorio.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }
        if (numEmpleado == null || numEmpleado.isBlank()) {
            throw new IllegalArgumentException("El número de empleado es obligatorio.");
        }
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
    }
}
