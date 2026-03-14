package com.s1.LogiTrack.controller;

import com.s1.LogiTrack.dto.response.AuditoriaResponseDTO;
import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.service.impl.AuditoriaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@RestController
@Tag(name = "Auditoría", description = "Aquí se puede consultar el historial de auditoría que deja el sistema.")
public class AuditoriaController {

    private final AuditoriaServiceImpl auditoriaService;

    @Operation(
            summary = "Listar todas las auditorías",
            description = "Muestra todos los registros de auditoría guardados en el sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de auditorías obtenido correctamente")
    @GetMapping("/public")
    public ResponseEntity<List<AuditoriaResponseDTO>> listarTodas() {
        return ResponseEntity.ok().body(auditoriaService.listarAuditorias());
    }

    @Operation(
            summary = "Buscar auditoría por id",
            description = "Permite consultar un registro de auditoría específico usando su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auditoría encontrada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe la auditoría")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaResponseDTO> buscarPorId(
            @Parameter(description = "Id de la auditoría a consultar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(auditoriaService.buscarPorId(id));
    }

    @Operation(
            summary = "Buscar auditorías por nombre de usuario",
            description = "Permite filtrar las auditorías usando el nombre del usuario que realizó la acción."
    )
    @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente")
    @GetMapping("/usuario")
    public ResponseEntity<List<AuditoriaResponseDTO>> buscarPorUsuario(
            @Parameter(description = "Nombre del usuario a buscar en la auditoría", example = "Juan")
            @RequestParam String nombre) {
        return ResponseEntity.ok().body(auditoriaService.buscarPorNombreUsuario(nombre));
    }

    @Operation(
            summary = "Buscar auditorías por operación",
            description = "Permite filtrar las auditorías según el tipo de operación realizada: INSERT, UPDATE o DELETE."
    )
    @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente")
    @GetMapping("/operacion")
    public ResponseEntity<List<AuditoriaResponseDTO>> buscarPorOperacion(
            @Parameter(description = "Tipo de operación de auditoría", example = "INSERT")
            @RequestParam OperacionAuditoria operacion) {
        return ResponseEntity.ok().body(auditoriaService.buscarPorOperacion(operacion));
    }
}
