package com.s1.LogiTrack.controller;


import com.s1.LogiTrack.dto.request.BodegaRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.service.impl.BodegaServiceImpl;
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
@RestController
@RequestMapping("/api/bodega")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bodega", description = "Aquí se maneja todo lo relacionado con las bodegas del sistema.")
public class BodegaController {

    private final BodegaServiceImpl bodegaService;

    @Operation(
            summary = "Registrar una bodega",
            description = "Sirve para guardar una bodega nueva con su nombre, ubicación, capacidad y encargado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bodega creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "404", description = "No existe el encargado indicado")
    })
    @PostMapping
    public ResponseEntity<BodegaResponseDTO> guardar(@Valid @RequestBody BodegaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.guardarBodega(dto));
    }

    @Operation(
            summary = "Actualizar una bodega",
            description = "Permite actualizar los datos de una bodega usando su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe la bodega o el encargado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BodegaResponseDTO> actualizar(
            @Valid @RequestBody BodegaRequestDTO dto,
            @Parameter(description = "Id de la bodega que se quiere actualizar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(bodegaService.actualizarBodega(dto, id));
    }

    @Operation(
            summary = "Listar todas las bodegas",
            description = "Muestra todas las bodegas registradas en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de bodegas obtenido correctamente")
    @GetMapping("/public")
    public ResponseEntity<List<BodegaResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(bodegaService.listarBodega());
    }

    @Operation(
            summary = "Buscar bodega por id",
            description = "Permite consultar una bodega específica por medio de su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bodega encontrada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe la bodega")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BodegaResponseDTO> buscarId(
            @Parameter(description = "Id de la bodega a consultar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(bodegaService.buscarPorId(id));
    }

    @Operation(
            summary = "Eliminar una bodega",
            description = "Elimina una bodega del sistema usando su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bodega eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe la bodega a eliminar")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id de la bodega que se quiere eliminar", example = "1")
            @PathVariable Long id) {
        bodegaService.eliminarBodega(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

