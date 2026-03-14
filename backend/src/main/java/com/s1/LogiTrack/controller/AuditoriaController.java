package com.s1.LogiTrack.controller;

import com.s1.LogiTrack.dto.response.AuditoriaResponseDTO;
import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.service.impl.AuditoriaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@RestController
public class AuditoriaController {
    private final AuditoriaServiceImpl auditoriaService;

    @GetMapping("/public")
    public ResponseEntity<List<AuditoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok().body(auditoriaService.listarAuditorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(auditoriaService.buscarPorId(id));
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<AuditoriaResponseDTO>> buscarPorUsuario(@RequestParam String nombre) {
        return ResponseEntity.ok().body(auditoriaService.buscarPorNombreUsuario(nombre));
    }

    @GetMapping("/operacion")
    public ResponseEntity<List<AuditoriaResponseDTO>> buscarPorOperacion(@RequestParam OperacionAuditoria operacion) {
        return ResponseEntity.ok().body(auditoriaService.buscarPorOperacion(operacion));
    }
}
