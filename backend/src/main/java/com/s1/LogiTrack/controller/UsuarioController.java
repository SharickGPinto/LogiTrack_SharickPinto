package com.s1.LogiTrack.controller;

import com.s1.LogiTrack.dto.request.UsuarioRequestDTO;
import com.s1.LogiTrack.dto.response.UsuarioResponseDTO;
import com.s1.LogiTrack.service.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {
    private final UsuarioServiceImpl usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> guardar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardarUsuario(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@Valid @RequestBody UsuarioRequestDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok().body(usuarioService.actualizarUsuario(dto, id));
    }

    @GetMapping("/public")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(usuarioService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
