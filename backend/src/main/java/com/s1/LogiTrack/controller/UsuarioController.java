package com.s1.LogiTrack.controller;

import com.s1.LogiTrack.dto.request.UsuarioRequestDTO;
import com.s1.LogiTrack.dto.response.UsuarioResponseDTO;
import com.s1.LogiTrack.service.impl.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuario", description = "Procesa todo lo relacionado con los usuarios")
@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;
    // EndPoints: Estos son los que me dice y administran que solicitud HTTP se va a usar.

    @PostMapping
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201",
                            description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400",
                            description = "Datos no válidos / body mal estructurado")
            }
    )
    public ResponseEntity<UsuarioResponseDTO> guardar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardarUsuario(dto));
    }

    @PutMapping("/{documento}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @Valid @RequestBody UsuarioRequestDTO dto,
            @Parameter(description = "Documento del usuario a actualizar", example = "1098765432")
            @PathVariable String documento) {
        return ResponseEntity.ok().body(usuarioService.actualizarUsuario(dto, documento));
    }

    @GetMapping("/public")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(usuarioService.listarUsuarios());
    }

    @Operation(summary = "Lista el usuario por documento",
            description = "Permite mostrar un usuario por parametro documento")
    @GetMapping("/{documento}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorDocumento(
            @Parameter(description = "Documento del usuario a consultar", example = "1098765432")
            @PathVariable String documento) {
        return ResponseEntity.ok().body(usuarioService.buscarPorDocumento(documento));
    }

    @DeleteMapping("/{documento}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Documento del usuario a eliminar", example = "1098765432")
            @PathVariable String documento) {
        usuarioService.eliminarUsuario(documento);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
