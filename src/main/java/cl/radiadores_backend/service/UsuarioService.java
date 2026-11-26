package cl.radiadores_backend.service;

import cl.radiadores_backend.dto.RegistroRequest;
import cl.radiadores_backend.model.Usuario;
import cl.radiadores_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Quien se registra desde la tienda siempre es cliente
    public Usuario registrar(RegistroRequest datos) {
        return crearUsuario(datos.getNombre(), datos.getApellidos(),
                datos.getCorreo(), datos.getClave(), "CLIENTE");
    }

    // Crea un usuario con el rol indicado 
    public Usuario crearUsuario(String nombre, String apellidos, String correo, String clave, String rol) {
        if (correo == null || correo.isBlank()) {
            throw new RuntimeException("El correo es obligatorio");
        }
        if (clave == null || clave.length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }
        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new RuntimeException("Ya existe una cuenta con ese correo");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave)); // se guarda encriptada
        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    // Compara la clave escrita con la encriptada de la base de datos
    public boolean claveCorrecta(String claveEscrita, Usuario usuario) {
        return passwordEncoder.matches(claveEscrita, usuario.getClave());
    }
}
