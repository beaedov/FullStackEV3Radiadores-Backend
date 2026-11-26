package cl.radiadores_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Lo que el backend responde al iniciar sesión: el token y los datos básicos
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String nombre;
    private String rol;
}