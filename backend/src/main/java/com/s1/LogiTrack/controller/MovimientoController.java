package com.s1.LogiTrack.controller;

import com.s1.LogiTrack.dto.request.MovimientoRequestDTO;
import com.s1.LogiTrack.dto.response.MovimientoResponseDTO;
import com.s1.LogiTrack.service.impl.MovimientoServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movimiento")
public class MovimientoController {
    private final MovimientoServiceImpl movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> guardar(@Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.guardarMovimiento(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> actualizar(@Valid @RequestBody MovimientoRequestDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok().body(movimientoService.actualizarMovimiento(dto, id));
    }

    @GetMapping("/public")
    public ResponseEntity<List<MovimientoResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(movimientoService.listarMovimientos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(movimientoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
