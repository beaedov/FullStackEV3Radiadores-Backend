package cl.radiadores_backend.service;

import cl.radiadores_backend.dto.ItemCompra;
import cl.radiadores_backend.model.*;
import cl.radiadores_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final DetalleBoletaRepository detalleRepository;
    private final ProductoRepository productoRepository;

    public BoletaService(BoletaRepository boletaRepository,
                         DetalleBoletaRepository detalleRepository,
                         ProductoRepository productoRepository) {
        this.boletaRepository = boletaRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
    }

    public List<Boleta> listar() {
        return boletaRepository.findAll();
    }

    public Boleta buscarPorId(Long id) {
        return boletaRepository.findById(id).orElse(null);
    }

    public List<DetalleBoleta> detalles(Long boletaId) {
        return detalleRepository.findByBoletaId(boletaId);
    }

    // Crea una compra: valida stock, descuenta stock y calcula el total.
    // @Transactional: si algo falla, se deshace todo (no queda media boleta guardada)
    @Transactional
    public Boleta comprar(Usuario usuario, List<ItemCompra> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Boleta boleta = new Boleta();
        boleta.setUsuario(usuario);
        boleta.setFecha(LocalDateTime.now());
        boleta.setTotal(0);
        boleta = boletaRepository.save(boleta);

        int total = 0;

        for (ItemCompra item : items) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no existe"));

            if (producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + producto.getNombre());
            }

            // Si tiene precio de oferta se usa ese 
            int precio = producto.getPrecioOferta() != null ? producto.getPrecioOferta() : producto.getPrecio();
            int subtotal = precio * item.getCantidad();

            // Descontar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boleta);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);
            detalleRepository.save(detalle);

            total = total + subtotal;
        }

        boleta.setTotal(total);
        return boletaRepository.save(boleta);
    }
}