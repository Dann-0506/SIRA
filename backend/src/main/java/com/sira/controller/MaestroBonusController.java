package com.sira.controller;

import com.sira.dto.BonusRequest;
import com.sira.dto.BonusResponse;
import com.sira.model.Usuario;
import com.sira.service.BonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maestro/inscripciones/{inscripcionId}/bonus")
@PreAuthorize("hasRole('MAESTRO')")
public class MaestroBonusController {

    @Autowired private BonusService bonusService;

    @GetMapping
    public List<BonusResponse> historial(@PathVariable Integer inscripcionId,
                                         @AuthenticationPrincipal Usuario usuario) {
        return bonusService.obtenerHistorial(inscripcionId).stream().map(BonusResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<BonusResponse> guardar(@PathVariable Integer inscripcionId,
                                                  @RequestBody BonusRequest request,
                                                  @AuthenticationPrincipal Usuario usuario) {
        BonusResponse response = BonusResponse.from(
                bonusService.guardar(inscripcionId, request.unidadId(), request.tipo(),
                        request.puntos(), request.justificacion())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
