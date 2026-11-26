package cl.radiadores_backend.dto;

import lombok.Data;

// Lo que el frontend envía al iniciar sesión
@Data
public class LoginRequest {
    private String correo;
    private String clave;
}
