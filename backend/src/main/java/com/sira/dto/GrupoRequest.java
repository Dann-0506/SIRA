package com.sira.dto;

public record GrupoRequest(
        Integer materiaId,
        Integer maestroId,
        String clave,
        Integer periodoId
) {}
