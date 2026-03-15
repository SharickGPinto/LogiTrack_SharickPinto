package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.ProductoRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.dto.response.ProductoResponseDTO;
import com.s1.LogiTrack.exception.BusinessRuleException;
import com.s1.LogiTrack.mapper.BodegaMapper;
import com.s1.LogiTrack.mapper.ProductoMapper;
import com.s1.LogiTrack.model.Auditoria;
import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.OperacionAuditoria;
import com.s1.LogiTrack.model.Producto;
import com.s1.LogiTrack.repository.AuditoriaRepository;
import com.s1.LogiTrack.repository.BodegaRepository;
import com.s1.LogiTrack.repository.ProductoRepository;
import com.s1.LogiTrack.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;
    private final AuditoriaRepository auditoriaRepository;

    @Override
    public ProductoResponseDTO guardarProducto(ProductoRequestDTO dto) {
        Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new EntityNotFoundException("No existe la bodega"));

        boolean existeProductoEnBodega = productoRepository.findByNombreIgnoreCase(dto.nombre()).stream()
                .anyMatch(producto -> producto.getBodega() != null
                        && producto.getBodega().getId().equals(dto.bodegaId()));
        if (existeProductoEnBodega) {
            throw new BusinessRuleException("Ya existe un producto con ese nombre en la bodega seleccionada");
        }

        Producto producto = productoMapper.DTOAEntidad(dto, bodega);
        Producto productoInsertado = productoRepository.save(producto);

        registrarAuditoria("Producto", OperacionAuditoria.INSERT, null, null, construirResumen(productoInsertado));
        BodegaResponseDTO bodegaDTO = bodegaMapper.entidadADTO(productoInsertado.getBodega());
        return productoMapper.entidadADTO(productoInsertado, bodegaDTO);
    }

    @Override
    public ProductoResponseDTO actualizarProducto(ProductoRequestDTO dto, Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicho producto a actualizar"));

        Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new EntityNotFoundException("No existe la bodega"));

        boolean existeOtroProductoEnBodega = productoRepository.findByNombreIgnoreCase(dto.nombre()).stream()
                .anyMatch(item -> item.getBodega() != null
                        && item.getBodega().getId().equals(dto.bodegaId())
                        && !item.getId().equals(producto.getId()));
        if (existeOtroProductoEnBodega) {
            throw new BusinessRuleException("Ya existe otro producto con ese nombre en la bodega seleccionada");
        }

        String valorAnterior = construirResumen(producto);
        productoMapper.actualizarEntidadDesdeDTO(producto, dto, bodega);
        Producto productoActualizado = productoRepository.save(producto);

        registrarAuditoria("Producto", OperacionAuditoria.UPDATE, null, valorAnterior, construirResumen(productoActualizado));
        BodegaResponseDTO bodegaDTO = bodegaMapper.entidadADTO(productoActualizado.getBodega());
        return productoMapper.entidadADTO(productoActualizado, bodegaDTO);
    }

    @Override
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicho producto a eliminar"));

        String valorAnterior = construirResumen(producto);
        productoRepository.delete(producto);
        registrarAuditoria("Producto", OperacionAuditoria.DELETE, null, valorAnterior, null);
    }

    @Override
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll().stream()
                .map(dato -> productoMapper.entidadADTO(
                        dato,
                        bodegaMapper.entidadADTO(
                                bodegaRepository.findById(dato.getBodega().getId())
                                        .orElseThrow(() -> new EntityNotFoundException("No existe la bodega"))
                        )
                ))
                .toList();
    }

    @Override
    public ProductoResponseDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicho producto"));

        Bodega bodega = bodegaRepository.findById(producto.getBodega().getId())
                .orElseThrow(() -> new EntityNotFoundException("No existe la bodega"));

        BodegaResponseDTO bodegaDTO = bodegaMapper.entidadADTO(bodega);
        return productoMapper.entidadADTO(producto, bodegaDTO);
    }

    @Override
    public List<ProductoResponseDTO> listarStockBajo() {
        return productoRepository.findByStockLessThan(10).stream()
                .map(dato -> productoMapper.entidadADTO(
                        dato,
                        bodegaMapper.entidadADTO(
                                bodegaRepository.findById(dato.getBodega().getId())
                                        .orElseThrow(() -> new EntityNotFoundException("No existe la bodega"))
                        )
                ))
                .toList();
    }

    private void registrarAuditoria(String entidad, OperacionAuditoria operacion, com.s1.LogiTrack.model.Usuario usuario, String valorAnterior, String valorNuevo) {
        Auditoria auditoria = new Auditoria();
        auditoria.setEntidad(entidad);
        auditoria.setOperacion(operacion);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setUsuario(usuario);
        auditoria.setValorAnterior(valorAnterior);
        auditoria.setValorNuevo(valorNuevo);
        auditoriaRepository.save(auditoria);
    }

    private String construirResumen(Producto producto) {
        return "id=" + producto.getId()
                + ", nombre=" + producto.getNombre()
                + ", categoria=" + producto.getCategoria()
                + ", precio=" + producto.getPrecio()
                + ", stock=" + producto.getStock()
                + ", bodega=" + (producto.getBodega() != null ? producto.getBodega().getNombre() : "Sin bodega");
    }
}
