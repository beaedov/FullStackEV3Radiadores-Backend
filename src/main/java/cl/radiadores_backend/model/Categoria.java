package cl.radiadores_backend.model;

import jakarta.persistence.*;
import lombok.Data;

//tabla categoria
@Entity
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
}
