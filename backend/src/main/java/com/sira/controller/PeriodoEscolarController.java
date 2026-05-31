package com.sira.controller;

import com.sira.model.PeriodoEscolar;
import com.sira.service.PeriodoEscolarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/periodos")
@PreAuthorize("hasRole('ADMIN')")
public class PeriodoEscolarController {

    @Autowired private PeriodoEscolarService periodoService;

    @GetMapping
    public List<PeriodoEscolar> listar() {
        return periodoService.listarTodos();
    }

    @GetMapping("/{id}")
    public PeriodoEscolar obtener(@PathVariable Integer id) {
        return periodoService.obtenerPorId(id);
    }

    @PostMapping
    public PeriodoEscolar crear(@RequestBody PeriodoEscolar periodo) {
        return periodoService.guardar(periodo);
    }

    @PutMapping("/{id}")
    public PeriodoEscolar actualizar(@PathVariable Integer id, @RequestBody PeriodoEscolar periodo) {
        periodo.setId(id);
        return periodoService.guardar(periodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        periodoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/actual")
    public ResponseEntity<Void> marcarComoActual(@PathVariable Integer id) {
        periodoService.marcarComoActual(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vigente")
    public PeriodoEscolar obtenerVigente() {
        return periodoService.obtenerPeriodoVigente();
    }
    
    /**
     * Mantenemos este endpoint temporalmente para compatibilidad con el dashboard/configuración actual 
     * mientras el frontend migra a la nueva estructura de periodos.
     */
    @GetMapping("/configuracion-legacy")
    public ResponseEntity<Map<String, Object>> obtenerLegacy() {
        try {
            PeriodoEscolar actual = periodoService.obtenerPeriodoVigente();
            return ResponseEntity.ok(Map.of(
                    "minimaAprobatoria", actual.getCalificacionMinimaAprobatoria(),
                    "maxima", actual.getCalificacionMaximaPosible(),
                    "semestreActivo", actual.getNombrePeriodo()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "minimaAprobatoria", 70,
                    "maxima", 100,
                    "semestreActivo", "SIN PERIODO ACTUAL"
            ));
        }
    }
}
