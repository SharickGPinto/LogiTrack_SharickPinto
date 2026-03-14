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
@Tag(name = "Producto", description = "Aquí va todo lo relacionado con los productos y su manejo dentro del inventario.")
@RestController
@RequestMapping("/api/producto")
@RequiredArgsConstructor
@Validated
public class ProductoController {

    private final ProductoServiceImpl productoService;

    @Operation(
            summary = "Registrar un producto",
            description = "Este endpoint permite guardar un producto nuevo con su nombre, categoría, precio, stock y la bodega a la que pertenece."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos o body mal estructurado"),
            @ApiResponse(responseCode = "404", description = "No existe la bodega indicada")
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> guardar(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardarProducto(dto));
    }

    @Operation(
            summary = "Actualizar un producto",
            description = "Permite actualizar la información de un producto usando su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe el producto o la bodega indicada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @Valid @RequestBody ProductoRequestDTO dto,
            @Parameter(description = "Id del producto a actualizar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(productoService.actualizarProducto(dto, id));
    }

    @Operation(
            summary = "Listar todos los productos",
            description = "Muestra todos los productos registrados dentro del sistema."
    )
    @ApiResponse(responseCode = "200", description = "Listado de productos obtenido correctamente")
    @GetMapping("/public")
    public ResponseEntity<List<ProductoResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(productoService.listarProductos());
    }

    @Operation(
            summary = "Listar productos con stock bajo",
            description = "Este endpoint muestra los productos que tienen menos de 10 unidades en stock."
    )
    @ApiResponse(responseCode = "200", description = "Listado de productos con stock bajo obtenido correctamente")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponseDTO>> listarStockBajo() {
        return ResponseEntity.ok().body(productoService.listarStockBajo());
    }

    @Operation(
            summary = "Buscar un producto por id",
            description = "Permite mostrar un producto específico usando su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe el producto")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarId(
            @Parameter(description = "Id del producto a consultar", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(productoService.buscarPorId(id));
    }

    @Operation(
            summary = "Eliminar un producto",
            description = "Elimina un producto del sistema por medio de su id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe el producto a eliminar")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Id del producto a eliminar", example = "1")
            @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
