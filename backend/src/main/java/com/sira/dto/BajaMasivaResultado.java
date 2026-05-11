package com.sira.dto;

import java.util.List;

public record BajaMasivaResultado(
        List<String> desactivados,
        List<String> noEncontrados,
        List<String> yaInactivos
) {}
