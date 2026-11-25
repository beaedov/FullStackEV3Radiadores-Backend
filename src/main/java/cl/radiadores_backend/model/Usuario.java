package cl.radiadores_backend.model;

import jakarta.persistence.*;
import lombok.Data;

//tabla usuario
@Entity
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellidos;
    private String correo;
    private String clave;
    private String rol; // "ADMIN" "VENDEDOR" "CLIENTE"
}
