package cl.radiadores_backend.dto;

import lombok.Data;

// Un producto del carrito: { "productoId": 1, "cantidad": 2 }
@Data
public class ItemCompra {
    private Long productoId;
    private Integer cantidad;
}