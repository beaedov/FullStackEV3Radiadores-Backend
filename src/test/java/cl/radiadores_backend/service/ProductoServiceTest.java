package cl.radiadores_backend.service;

import cl.radiadores_backend.model.Producto;
import cl.radiadores_backend.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    // Producto válido para reutilizar
    private Producto productoValido() {
        Producto p = new Producto();
        p.setNombre("Refrigerante 4L");
        p.setPrecio(14990);
        p.setStock(5);
        return p;
    }

    // guardar: precio inválido
    @Test
    void guardar_precioCero_lanzaExcepcion() {
        Producto p = productoValido();
        p.setPrecio(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.guardar(p));

        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
        verify(productoRepository, never()).save(any());
    }

    // guardar: stock negativo
    @Test
    void guardar_stockNegativo_lanzaExcepcion() {
        Producto p = productoValido();
        p.setStock(-1);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.guardar(p));

        assertEquals("El stock no puede ser negativo", ex.getMessage());
        verify(productoRepository, never()).save(any());
    }

    // guardar: producto válido
    @Test
    void guardar_productoValido_guardaEnRepositorio() {
        Producto p = productoValido();
        when(productoRepository.save(p)).thenReturn(p);

        Producto guardado = productoService.guardar(p);

        assertEquals("Refrigerante 4L", guardado.getNombre());
        verify(productoRepository).save(p);
    }

    // actualizar: producto inexistente
    @Test
    void actualizar_productoNoExiste_devuelveNull() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.actualizar(99L, productoValido());

        assertNull(resultado);
        verify(productoRepository, never()).save(any());
    }

    // actualizar: producto existente conserva el id
    @Test
    void actualizar_productoExiste_mantieneElId() {
        Producto existente = productoValido();
        existente.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto resultado = productoService.actualizar(1L, productoValido());

        assertEquals(1L, resultado.getId());
    }

    // eliminar: producto inexistente
    @Test
    void eliminar_productoNoExiste_devuelveFalse() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertFalse(productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(any());
    }

    // eliminar: producto existente
    @Test
    void eliminar_productoExiste_devuelveTrue() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        assertTrue(productoService.eliminar(1L));
        verify(productoRepository).deleteById(1L);
    }
}