package cl.radiadores_backend.service;

import cl.radiadores_backend.dto.ItemCompra;
import cl.radiadores_backend.model.Boleta;
import cl.radiadores_backend.model.Producto;
import cl.radiadores_backend.model.Usuario;
import cl.radiadores_backend.repository.BoletaRepository;
import cl.radiadores_backend.repository.DetalleBoletaRepository;
import cl.radiadores_backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;
    @Mock
    private DetalleBoletaRepository detalleRepository;
    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private BoletaService boletaService;

    // Carrito vacío
    @Test
    void comprar_carritoVacio_lanzaExcepcion() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.comprar(new Usuario(), List.of()));

        assertEquals("El carrito está vacío", ex.getMessage());
        verify(boletaRepository, never()).save(any());
    }

    // Precio de oferta y descuento de stock
    @Test
    void comprar_conOferta_calculaTotalYDescuentaStock() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Radiador Toyota Yaris");
        producto.setPrecio(89990);
        producto.setPrecioOferta(74990);
        producto.setStock(8);

        ItemCompra item = new ItemCompra();
        item.setProductoId(1L);
        item.setCantidad(2);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

        Boleta boleta = boletaService.comprar(new Usuario(), List.of(item));

        assertEquals(149980, boleta.getTotal()); // 74990 x 2
        assertEquals(6, producto.getStock());    // 8 - 2
    }

    // Stock insuficiente
    @Test
    void comprar_stockInsuficiente_lanzaExcepcionYNoDescuenta() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Radiador Toyota Yaris");
        producto.setPrecio(89990);
        producto.setStock(1);

        ItemCompra item = new ItemCompra();
        item.setProductoId(1L);
        item.setCantidad(5);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.comprar(new Usuario(), List.of(item)));

        assertEquals("Stock insuficiente para Radiador Toyota Yaris", ex.getMessage());
        assertEquals(1, producto.getStock());                  // stock sin cambios
        verify(productoRepository, never()).save(any());       // no se guardó el producto
    }

    // Producto inexistente
    @Test
    void comprar_productoNoExiste_lanzaExcepcion() {
        ItemCompra item = new ItemCompra();
        item.setProductoId(99L);
        item.setCantidad(1);

        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.comprar(new Usuario(), List.of(item)));

        assertEquals("Producto no existe", ex.getMessage());
    }
}