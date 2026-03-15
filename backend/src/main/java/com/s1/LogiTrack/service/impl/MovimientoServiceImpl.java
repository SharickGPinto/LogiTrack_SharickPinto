package com.s1.LogiTrack.service.impl;

import com.s1.LogiTrack.dto.request.MovimientoDetalleRequestDTO;
import com.s1.LogiTrack.dto.request.MovimientoRequestDTO;
import com.s1.LogiTrack.dto.response.MovimientoResponseDTO;
import com.s1.LogiTrack.exception.BusinessRuleException;
import com.s1.LogiTrack.mapper.MovimientoDetalleMapper;
import com.s1.LogiTrack.mapper.MovimientoMapper;
import com.s1.LogiTrack.model.*;
import com.s1.LogiTrack.repository.*;
import com.s1.LogiTrack.service.MovimientoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoMapper movimientoMapper;
    private final MovimientoDetalleMapper movimientoDetalleMapper;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final AuditoriaRepository auditoriaRepository;

    @Override
    @Transactional
    public MovimientoResponseDTO guardarMovimiento(MovimientoRequestDTO dto) {
            Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("No existe el usuario"));
            Bodega bodegaOrigen = obtenerBodega(dto.bodegaOrigenId(), "No existe la bodega origen");
            Bodega bodegaDestino = obtenerBodega(dto.bodegaDestinoId(), "No existe la bodega destino");
            validarConfiguracionMovimiento(dto.tipoMovimiento(), bodegaOrigen, bodegaDestino);

            // PASO 1: validar todo antes de modificar nada
            List<Producto> productosValidados = new ArrayList<>();
            for (MovimientoDetalleRequestDTO detalleDTO : dto.detalles()) {
                Producto producto = productoRepository.findById(detalleDTO.productoId())
                        .orElseThrow(() -> new EntityNotFoundException("No existe el producto"));
                validarProductoSegunMovimiento(dto.tipoMovimiento(), producto, bodegaOrigen, bodegaDestino);
                if (dto.tipoMovimiento() == TipoMovimiento.SALIDA || dto.tipoMovimiento() == TipoMovimiento.TRANSFERENCIA) {
                    if (producto.getStock() < detalleDTO.cantidad()) {
                        throw new BusinessRuleException("No hay stock suficiente para el producto: " + producto.getNombre());
                    }
                }
                productosValidados.add(producto);
            }

            // PASO 2: todo valido, crear y guardar
            List<MovimientoDetalle> detalles = new ArrayList<>();
            Movimiento movimiento = movimientoMapper.DTOAEntidad(dto, usuario, bodegaOrigen, bodegaDestino, detalles);

            for (int i = 0; i < dto.detalles().size(); i++) {
                MovimientoDetalleRequestDTO detalleDTO = dto.detalles().get(i);
                Producto producto = productosValidados.get(i);
                aplicarMovimientoEnStock(dto.tipoMovimiento(), producto, bodegaDestino, detalleDTO.cantidad());
                MovimientoDetalle detalle = movimientoDetalleMapper.DTOAEntidad(detalleDTO, producto, movimiento);
                detalles.add(detalle);
            }

            Movimiento movimientoInsertado = movimientoRepository.save(movimiento);
            registrarAuditoria("Movimiento", OperacionAuditoria.INSERT, usuario, null, construirResumen(movimientoInsertado));
            return movimientoMapper.entidadADTO(movimientoInsertado);
        }

    @Override
    @Transactional
    public MovimientoResponseDTO actualizarMovimiento(MovimientoRequestDTO dto, Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicho movimiento a actualizar"));
        String valorAnterior = construirResumen(movimiento);

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("No existe el usuario"));
        Bodega bodegaOrigen = obtenerBodega(dto.bodegaOrigenId(), "No existe la bodega origen");
        Bodega bodegaDestino = obtenerBodega(dto.bodegaDestinoId(), "No existe la bodega destino");
        validarConfiguracionMovimiento(dto.tipoMovimiento(), bodegaOrigen, bodegaDestino);

        // PASO 1: validar productos del nuevo estado antes de revertir
        List<Producto> productosNuevos = new ArrayList<>();
        for (MovimientoDetalleRequestDTO detalleDTO : dto.detalles()) {
            Producto producto = productoRepository.findById(detalleDTO.productoId())
                    .orElseThrow(() -> new EntityNotFoundException("No existe el producto"));
            validarProductoSegunMovimiento(dto.tipoMovimiento(), producto, bodegaOrigen, bodegaDestino);
            productosNuevos.add(producto);
        }

        // PASO 2: validaciones ok, ahora si revertir
        revertirMovimientoEnStock(movimiento);

        // PASO 3: aplicar nuevo estado
        List<MovimientoDetalle> detalles = new ArrayList<>();
        for (int i = 0; i < dto.detalles().size(); i++) {
            MovimientoDetalleRequestDTO detalleDTO = dto.detalles().get(i);
            Producto producto = productosNuevos.get(i);
            aplicarMovimientoEnStock(dto.tipoMovimiento(), producto, bodegaDestino, detalleDTO.cantidad());
            MovimientoDetalle detalle = movimientoDetalleMapper.DTOAEntidad(detalleDTO, producto, movimiento);
            detalles.add(detalle);
        }

        movimientoMapper.actualizarEntidadDesdeDTO(movimiento, dto, usuario, bodegaOrigen, bodegaDestino, detalles);
        Movimiento movimientoActualizado = movimientoRepository.save(movimiento);
        registrarAuditoria("Movimiento", OperacionAuditoria.UPDATE, usuario, valorAnterior, construirResumen(movimientoActualizado));
        return movimientoMapper.entidadADTO(movimientoActualizado);
    }

    @Override
    public List<MovimientoResponseDTO> listarMovimientos() {
        return movimientoRepository.findAll().stream()
                .map(movimientoMapper::entidadADTO)
                .toList();
    }

    @Override
    public MovimientoResponseDTO buscarPorId(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe dicho movimiento"));

        return movimientoMapper.entidadADTO(movimiento);
    }

    @Override
    @Transactional
    public void eliminarMovimiento(Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el movimiento a eliminar"));

        String valorAnterior = construirResumen(movimiento);
        revertirMovimientoEnStock(movimiento);
        movimientoRepository.delete(movimiento);
        registrarAuditoria("Movimiento", OperacionAuditoria.DELETE, movimiento.getUsuario(), valorAnterior, null);
    }

    private Bodega obtenerBodega(Long id, String mensajeError) {
        if (id == null) {
            return null;
        }
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(mensajeError));
    }

    private void validarConfiguracionMovimiento(TipoMovimiento tipoMovimiento, Bodega bodegaOrigen, Bodega bodegaDestino) {
        if (tipoMovimiento == TipoMovimiento.ENTRADA) {
            if (bodegaDestino == null) {
                throw new BusinessRuleException("La bodega destino es obligatoria para una entrada");
            }
            if (bodegaOrigen != null) {
                throw new BusinessRuleException("Una entrada no debe tener bodega origen");
            }
        }

        if (tipoMovimiento == TipoMovimiento.SALIDA) {
            if (bodegaOrigen == null) {
                throw new BusinessRuleException("La bodega origen es obligatoria para una salida");
            }
            if (bodegaDestino != null) {
                throw new BusinessRuleException("Una salida no debe tener bodega destino");
            }
        }

        if (tipoMovimiento == TipoMovimiento.TRANSFERENCIA) {
            if (bodegaOrigen == null || bodegaDestino == null) {
                throw new BusinessRuleException("La transferencia requiere bodega origen y bodega destino");
            }
            if (Objects.equals(bodegaOrigen.getId(), bodegaDestino.getId())) {
                throw new BusinessRuleException("La bodega origen y la bodega destino no pueden ser la misma");
            }
        }
    }

    private void validarProductoSegunMovimiento(TipoMovimiento tipoMovimiento, Producto producto, Bodega bodegaOrigen, Bodega bodegaDestino) {
        if (tipoMovimiento == TipoMovimiento.ENTRADA) {
            if (producto.getBodega() == null || !producto.getBodega().getId().equals(bodegaDestino.getId())) {
                throw new BusinessRuleException("El producto de una entrada debe pertenecer a la bodega destino");
            }
        }

        if (tipoMovimiento == TipoMovimiento.SALIDA || tipoMovimiento == TipoMovimiento.TRANSFERENCIA) {
            if (producto.getBodega() == null || !producto.getBodega().getId().equals(bodegaOrigen.getId())) {
                throw new BusinessRuleException("El producto seleccionado no pertenece a la bodega origen");
            }
        }
    }

    private void aplicarMovimientoEnStock(TipoMovimiento tipoMovimiento, Producto producto, Bodega bodegaDestino, Integer cantidad) {
        if (tipoMovimiento == TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + cantidad);
            productoRepository.save(producto);
            return;
        }

        if (tipoMovimiento == TipoMovimiento.SALIDA) {
            if (producto.getStock() < cantidad) {
                throw new BusinessRuleException("No hay stock suficiente para realizar la salida");
            }
            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);
            return;
        }

        if (producto.getStock() < cantidad) {
            throw new BusinessRuleException("No hay stock suficiente para realizar la transferencia");
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        Producto productoDestino = buscarProductoEnBodegaDestino(producto, bodegaDestino);
        if (productoDestino == null) {
            productoDestino = new Producto();
            productoDestino.setNombre(producto.getNombre());
            productoDestino.setCategoria(producto.getCategoria());
            productoDestino.setPrecio(producto.getPrecio());
            productoDestino.setStock(0);
            productoDestino.setBodega(bodegaDestino);
        }
        productoDestino.setStock(productoDestino.getStock() + cantidad);
        productoRepository.save(productoDestino);
    }

    private void revertirMovimientoEnStock(Movimiento movimiento) {
        for (MovimientoDetalle detalle : movimiento.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new EntityNotFoundException("No existe el producto del detalle"));
            Integer cantidad = detalle.getCantidad();

            if (movimiento.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
                if (producto.getStock() < cantidad) {
                    throw new BusinessRuleException("No se puede revertir la entrada porque el stock actual es menor al registrado");
                }
                producto.setStock(producto.getStock() - cantidad);
                productoRepository.save(producto);
                continue;
            }

            if (movimiento.getTipoMovimiento() == TipoMovimiento.SALIDA) {
                producto.setStock(producto.getStock() + cantidad);
                productoRepository.save(producto);
                continue;
            }

            producto.setStock(producto.getStock() + cantidad);
            productoRepository.save(producto);

            Producto productoDestino = buscarProductoEnBodegaDestino(producto, movimiento.getBodegaDestino());
            if (productoDestino == null || productoDestino.getStock() < cantidad) {
                throw new BusinessRuleException("No se puede revertir la transferencia porque el stock en destino no coincide");
            }
            productoDestino.setStock(productoDestino.getStock() - cantidad);
            productoRepository.save(productoDestino);
        }
    }

    private Producto buscarProductoEnBodegaDestino(Producto productoOrigen, Bodega bodegaDestino) {
        return productoRepository.findByNombreIgnoreCase(productoOrigen.getNombre()).stream()
                .filter(item -> item.getBodega() != null
                        && item.getBodega().getId().equals(bodegaDestino.getId())
                        && Objects.equals(item.getCategoria(), productoOrigen.getCategoria())
                        && Objects.equals(item.getPrecio(), productoOrigen.getPrecio()))
                .findFirst()
                .orElse(null);
    }

    private void registrarAuditoria(String entidad, OperacionAuditoria operacion, Usuario usuario, String valorAnterior, String valorNuevo) {
        Auditoria auditoria = new Auditoria();
        auditoria.setEntidad(entidad);
        auditoria.setOperacion(operacion);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setUsuario(usuario);
        auditoria.setValorAnterior(valorAnterior);
        auditoria.setValorNuevo(valorNuevo);
        auditoriaRepository.save(auditoria);
    }

    private String construirResumen(Movimiento movimiento) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("id=").append(movimiento.getId())
                .append(", tipo=").append(movimiento.getTipoMovimiento())
                .append(", usuario=").append(movimiento.getUsuario() != null ? movimiento.getUsuario().getNombre() : "Sin usuario")
                .append(", origen=").append(movimiento.getBodegaOrigen() != null ? movimiento.getBodegaOrigen().getNombre() : "Sin origen")
                .append(", destino=").append(movimiento.getBodegaDestino() != null ? movimiento.getBodegaDestino().getNombre() : "Sin destino")
                .append(", detalles=");

        for (MovimientoDetalle detalle : movimiento.getDetalles()) {
            resumen.append("[")
                    .append(detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Sin producto")
                    .append(": ")
                    .append(detalle.getCantidad())
                    .append("]");
        }

        return resumen.toString();
    }
}