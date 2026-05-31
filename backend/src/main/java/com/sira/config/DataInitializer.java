package com.sira.config;

import com.sira.model.Administrador;
import com.sira.model.PeriodoEscolar;
import com.sira.model.Usuario;
import com.sira.repository.AdministradorRepository;
import com.sira.repository.PeriodoEscolarRepository;
import com.sira.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private PeriodoEscolarRepository periodoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        crearAdminPorDefecto();
        crearPeriodoInicialPorDefecto();
    }

    private void crearAdminPorDefecto() {
        if (usuarioRepository.existsByEmail("admin@escuela.edu")) return;

        Usuario usuario = usuarioRepository.save(new Usuario(
                "Administrador",
                "Sistema",
                null,
                "admin@escuela.edu",
                passwordEncoder.encode("123456"),
                "ADMIN",
                LocalDate.of(1990, 1, 1)
        ));
        administradorRepository.save(new Administrador(usuario, "ADMIN-001"));
    }

    private void crearPeriodoInicialPorDefecto() {
        if (periodoRepository.count() > 0) return;

        int anio = LocalDate.now().getYear();
        int mes = LocalDate.now().getMonthValue();
        
        String nombre;
        LocalDate inicio;
        LocalDate fin;

        if (mes <= 6) {
            nombre = "ENERO - JUNIO " + anio;
            inicio = LocalDate.of(anio, 1, 1);
            fin = LocalDate.of(anio, 6, 30);
        } else {
            nombre = "AGOSTO - DICIEMBRE " + anio;
            inicio = LocalDate.of(anio, 8, 1);
            fin = LocalDate.of(anio, 12, 31);
        }

        PeriodoEscolar periodo = new PeriodoEscolar(nombre, inicio, fin);
        periodo.setCalificacionMinimaAprobatoria(new BigDecimal("70.00"));
        periodo.setCalificacionMaximaPosible(new BigDecimal("100.00"));
        periodo.setEsPeriodoActual(true);

        periodoRepository.save(periodo);
    }
}
