package cl.radiadores_backend.controller;

import cl.radiadores_backend.dto.AuthResponse;
import cl.radiadores_backend.dto.LoginRequest;
import cl.radiadores_backend.dto.RegistroRequest;
import cl.radiadores_backend.model.Usuario;
import cl.radiadores_backend.security.JwtUtil;
import cl.radiadores_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    // crea una cuenta CLIENTE y devuelve el token
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest datos) {
        try {
            Usuario usuario = usuarioService.registrar(datos);
            String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
            return ResponseEntity.status(201)
                    .body(new AuthResponse(token, usuario.getNombre(), usuario.getRol()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        }
    }

    // valida correo y clave, devuelve el token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos) {
        Usuario usuario = usuarioService.buscarPorCorreo(datos.getCorreo());

        if (usuario == null || !usuarioService.claveCorrecta(datos.getClave(), usuario)) {
            return ResponseEntity.status(401).body("Correo o contraseña incorrectos");
        }

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
        return ResponseEntity.ok(new AuthResponse(token, usuario.getNombre(), usuario.getRol()));
    }
}