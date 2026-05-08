package com.sira.controller;

import com.sira.dto.CarreraRequest;
import com.sira.dto.CarreraResponse;
import com.sira.service.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/carreras")
@PreAuthorize("hasRole('ADMIN')")
public class CarreraController {

    @Autowired private CarreraService carreraService;

    @GetMapping
    public List<CarreraResponse> listar() {
        return carreraService.listarTodas().stream().map(CarreraResponse::from).toList();
    }

    @GetMapping("/activas")
    @PreAuthorize("hasAnyRole('ADMIN', 'MAESTRO')")
    public List<CarreraResponse> listarActivas() {
        return carreraService.listarActivas().stream().map(CarreraResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CarreraResponse obtener(@PathVariable Integer id) {
        return CarreraResponse.from(carreraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CarreraResponse> crear(@RequestBody CarreraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CarreraResponse.from(carreraService.crear(request.clave(), request.nombre())));
    }

    @PutMapping("/{id}")
    public CarreraResponse actualizar(@PathVariable Integer id, @RequestBody CarreraRequest request) {
        return CarreraResponse.from(carreraService.actualizar(id, request.clave(), request.nombre()));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Integer id, @RequestParam boolean activa) {
        carreraService.cambiarEstado(id, activa);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        carreraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
