package cl.radiadores_backend.model;

import jakarta.persistence.*;
import lombok.Data;

//tabla producto
@Entity
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer precio;
    private Integer precioOferta;
    private Integer stock;
    private Integer stockCritico;
    private String descripcion;
    private String imagen;

    // muchos productos pertenecen a una categoría
    @ManyToOne
    private Categoria categoria;
}