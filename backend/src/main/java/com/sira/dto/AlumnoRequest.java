package com.sira.dto;

import java.time.LocalDate;

public record AlumnoRequest(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String numControl,
        String curp,
        LocalDate fechaNacimiento,
        Integer carreraId
) {}
