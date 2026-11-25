package cl.radiadores_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DetalleBoleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;

    // Muchos detalles pertenecen a una boleta
    @ManyToOne
    private Boleta boleta;

    // Cada detalle es de un producto
    @ManyToOne
    private Producto producto;
}
