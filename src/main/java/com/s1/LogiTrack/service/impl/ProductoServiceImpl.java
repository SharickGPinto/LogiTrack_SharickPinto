package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.ProductoRequestDTO;
import com.s1.LogiTrack.dto.response.BodegaResponseDTO;
import com.s1.LogiTrack.dto.response.ProductoResponseDTO;
import com.s1.LogiTrack.mapper.BodegaMapper;
import com.s1.LogiTrack.mapper.ProductoMapper;
import com.s1.LogiTrack.model.Bodega;
import com.s1.LogiTrack.model.Producto;
import com.s1.LogiTrack.repository.BodegaRepository;
import com.s1.LogiTrack.repository.ProductoRepository;
import com.s1.LogiTrack.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
     final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;



    @Override
    public ProductoResponseDTO guardarProducto(ProductoRequestDTO dto) {
        Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new RuntimeException("No existe la bodega"));

        Producto p = productoMapper.DTOAEntidad(dto, bodega);
        Producto pInsertada = productoRepository.save(p);

        BodegaResponseDTO bodegaDTO = bodegaMapper.entidadADTO(pInsertada.getBodega());
        return productoMapper.entidadADTO(pInsertada, bodegaDTO);
    }

    @Override
    public ProductoResponseDTO actualizarProducto(ProductoRequestDTO dto, Long id) {
            Producto p = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("No existe dicho producto a actualizar"));

            Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                    .orElseThrow(() -> new RuntimeException("No existe la bodega"));

            productoMapper.actualizarEntidadDesdeDTO(p, dto, bodega);
            Producto pActualizar = productoRepository.save(p);

            BodegaResponseDTO bodegaDTO = bodegaMapper.entidadADTO(pActualizar.getBodega());

            return productoMapper.entidadADTO(pActualizar, bodegaDTO);
        }

    @Override
    public void eliminarProducto(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho producto a eliminar"));

        productoRepository.delete(p);
    }

    @Override
    public List<ProductoResponseDTO> listarProductos() {
        return productoRepository.findAll().stream()
                .map(dato -> productoMapper.entidadADTO(
                        dato,
                        bodegaMapper.entidadADTO(
                                bodegaRepository.findById(dato.getBodega().getId())
                                        .orElseThrow(() -> new RuntimeException("No existe la bodega"))
                        )
                ))
                .toList();
    }

    @Override
    public ProductoResponseDTO buscarPorId(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe dicho producto"));

        Bodega bodega = bodegaRepository.findById(p.getBodega().getId())
                .orElseThrow(() -> new RuntimeException("No existe la bodega"));

        BodegaResponseDTO bodegaDTO = bodegaMapper.entidadADTO(bodega);

        return productoMapper.entidadADTO(p, bodegaDTO);
    }

    @Override
    public List<ProductoResponseDTO> listarStockBajo() {
        return productoRepository.findByStockLessThan(10).stream()
                .map(dato -> productoMapper.entidadADTO(
                        dato,
                        bodegaMapper.entidadADTO(
                                bodegaRepository.findById(dato.getBodega().getId())
                                        .orElseThrow(() -> new RuntimeException("No existe la bodega"))
                        )
                ))
                .toList();
    }
}
