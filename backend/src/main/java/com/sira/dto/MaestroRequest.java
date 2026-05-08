package com.sira.dto;

import java.time.LocalDate;

public record MaestroRequest(
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        String email,
        String numEmpleado,
        LocalDate fechaNacimiento
) {}
