package com.sira.dto;

import java.math.BigDecimal;

public record ActividadGrupoRequest(Integer unidadId, String nombre, BigDecimal ponderacion) {}
