package cl.radiadores_backend.config;

import cl.radiadores_backend.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Se ejecuta una vez cada vez que arranca la aplicación
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioService usuarioService;

    public DatosIniciales(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        // Solo los crea si todavía no existen
        if (usuarioService.buscarPorCorreo("admin@radiadores.cl") == null) {
            usuarioService.crearUsuario("Admin", "Radiadores", "admin@radiadores.cl", "admin123", "ADMIN");
        }
        if (usuarioService.buscarPorCorreo("vendedor@radiadores.cl") == null) {
            usuarioService.crearUsuario("Vendedor", "Radiadores", "vendedor@radiadores.cl", "vendedor123", "VENDEDOR");
        }
    }
}
