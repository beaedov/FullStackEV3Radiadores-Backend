package cl.radiadores_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Se leen de application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expiracion;

    private SecretKey clave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Crea el token con el correo y el rol dentro, firmado con la clave secreta
    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracion))
                .signWith(clave())
                .compact();
    }

    private Claims leer(String token) {
        return Jwts.parser().verifyWith(clave()).build()
                .parseSignedClaims(token).getPayload();
    }

    public String obtenerCorreo(String token) {
        return leer(token).getSubject();
    }

    public String obtenerRol(String token) {
        return leer(token).get("rol", String.class);
    }

    // Un token es válido si la firma es correcta y no ha expirado
    public boolean esValido(String token) {
        try {
            leer(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
