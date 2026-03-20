package com.s1.LogiTrack.controller;

import com.s1.LogiTrack.dto.request.MovimientoRequestDTO;
import com.s1.LogiTrack.dto.response.MovimientoResponseDTO;
import com.s1.LogiTrack.dto.response.ProductoResponseDTO;
import com.s1.LogiTrack.service.impl.MovimientoServiceImpl;
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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movimiento")
@Tag(name = "Movimiento", description = "Aquí se registran y consultan los movimientos del inventario.")
public class MovimientoController {

    private final MovimientoServiceImpl movimientoService;

    @Operation(
            summary = "Registrar un movimiento",
            description = "Permite guardar un movimiento de inventario. Puede ser de tipo ENTRADA, SALIDA o TRANSFERENCIA, junto con sus detalles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "404", description = "No existe el usuario, producto o alguna bodega indicada")
    })
    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> guardar(@Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.guardarMovimiento(dto));
    }

    @Operation(
            summary = "Actualizar un movimiento",
            description = "Permite modificar un movimiento ya registrado usando su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe el movimiento, usuario, producto o bodega indicada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> actualizar(
            @Valid @RequestBody MovimientoRequestDTO dto,
            @Parameter(description = "Id del movimiento que se quiere actualizar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(movimientoService.actualizarMovimiento(dto, id));
    }

    @Operation(
            summary = "Listar todos los movimientos",
            description = "Muestra el historial completo de movimientos registrados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de movimientos obtenido correctamente")
    @GetMapping("/public")
    public ResponseEntity<List<MovimientoResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(movimientoService.listarMovimientos());
    }

    @Operation(
            summary = "Buscar movimiento por id",
            description = "Permite consultar un movimiento específico usando su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe el movimiento")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> buscarPorId(
            @Parameter(description = "Id del movimiento a consultar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(movimientoService.buscarPorId(id));
    }

    @Operation(
            summary = "Eliminar un movimiento",
            description = "Elimina un movimiento del sistema por medio de su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe el movimiento a eliminar")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id del movimiento que se quiere eliminar", example = "1")
            @PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @Operation(
            summary = "Listar los ultimos movimientos registrados",
            description = "Este endpoint muestra los ultimos 10 movimientos."
    )
    @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente")
    @GetMapping("/movimientos/recientes")
    public ResponseEntity<List<MovimientoResponseDTO>> listarRecientes(
         @Parameter(description = "Se debe retornar los 10 ultims moviminientos registrados")
         @PathVariable Long id){
        return ResponseEntity.ok().body(movimientoService.listarRecientes());
    }
}
