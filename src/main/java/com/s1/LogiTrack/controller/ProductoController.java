package com.s1.LogiTrack.controller;

import com.s1.LogiTrack.dto.request.ProductoRequestDTO;
import com.s1.LogiTrack.dto.response.ProductoResponseDTO;
import com.s1.LogiTrack.service.impl.ProductoServiceImpl;
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
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
@Tag(name = "Producto", description = "Procesa todo lo relacionado con productos")
@RestController
@RequestMapping("/api/producto")
@RequiredArgsConstructor
@Validated
public class ProductoController {

    private final ProductoServiceImpl productoService;
    @PostMapping
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos no válidos / body mal estructurado")
            }
    )
    public ResponseEntity<ProductoResponseDTO> guardar(@Valid @RequestBody ProductoRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardarProducto(dto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@Valid @RequestBody ProductoRequestDTO dto,@Parameter(description = "Id del producto a actualizar", example = "1") @PathVariable Long id){
        return ResponseEntity.ok().body(productoService.actualizarProducto(dto, id));
    }


    @GetMapping("/public")
    public ResponseEntity<List<ProductoResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(productoService.listarProductos());
    }
    @Operation(summary = "Listar los productos bajos de stock",
    description = "Se espera poder filtrar quienes cumplen con menor producto")
        @GetMapping("/stock-bajo")
        public ResponseEntity<List<ProductoResponseDTO>> listarStockBajo(){
            return ResponseEntity.ok().body(productoService.listarStockBajo());
        }


        @Operation(summary = "Buscar un producto por id",
        description = "Permite mostrar un producto por parametro  id tipo entero ")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarId(@PathVariable Long id){
        return ResponseEntity.ok().body(productoService.buscarPorId(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Parameter(description = "Id del producto a eliminar", example = "1")@PathVariable Long id){
        productoService.eliminarProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
