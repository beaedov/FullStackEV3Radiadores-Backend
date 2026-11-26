package cl.radiadores_backend.dto;

import lombok.Data;

// Lo que el frontend envía al crear una cuenta
@Data
public class RegistroRequest {
    private String nombre;
    private String apellidos;
    private String correo;
    private String clave;
}
