package com.s1.LogiTrack.controller;


import com.s1.LogiTrack.dto.request.BodegaRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.service.impl.BodegaServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
@Validated
public class BodegaController {
        private final BodegaServiceImpl bodegaService;
        @PostMapping
        public ResponseEntity<BodegaResponseDTO> guardar(@Valid @RequestBody BodegaRequestDTO dto){
            return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.guardarBodega(dto));
        }
        @PutMapping("/{id}")
        public ResponseEntity<BodegaResponseDTO> actualizar(@Valid @RequestBody BodegaRequestDTO dto, @PathVariable Long id){
            return ResponseEntity.ok().body(bodegaService.actualizarBodega(dto, id));
        }
        @GetMapping("/public")
        public ResponseEntity<List<BodegaResponseDTO>> listarTodos() {
            return ResponseEntity.ok().body(bodegaService.listarBodega());
        }

        @GetMapping("/{id}")
        public ResponseEntity<BodegaResponseDTO> buscarId(@PathVariable Long id){
            return ResponseEntity.ok().body(bodegaService.buscarPorId(id));
        }
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> eliminar(@PathVariable Long id){
            bodegaService.eliminarBodega(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

