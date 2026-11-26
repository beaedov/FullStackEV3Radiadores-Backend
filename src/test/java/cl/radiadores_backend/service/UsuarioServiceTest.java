package cl.radiadores_backend.service;

import cl.radiadores_backend.dto.RegistroRequest;
import cl.radiadores_backend.model.Usuario;
import cl.radiadores_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // Datos de registro válidos para reutilizar
    private RegistroRequest datosValidos() {
        RegistroRequest r = new RegistroRequest();
        r.setNombre("Ana");
        r.setApellidos("Pérez");
        r.setCorreo("ana@correo.cl");
        r.setClave("abc123");
        return r;
    }

    // registrar: datos válidos
    @Test
    void registrar_datosValidos_creaClienteConClaveEncriptada() {
        when(passwordEncoder.encode("abc123")).thenReturn("HASH");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario usuario = usuarioService.registrar(datosValidos());

        assertEquals("CLIENTE", usuario.getRol());
        assertEquals("HASH", usuario.getClave()); // no se guarda en texto plano
        assertEquals("ana@correo.cl", usuario.getCorreo());
    }

    // registrar: clave corta
    @Test
    void registrar_claveCorta_lanzaExcepcion() {
        RegistroRequest datos = datosValidos();
        datos.setClave("123");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(datos));

        assertEquals("La contraseña debe tener al menos 6 caracteres", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // registrar: correo duplicado
    @Test
    void registrar_correoDuplicado_lanzaExcepcion() {
        when(usuarioRepository.findByCorreo("ana@correo.cl")).thenReturn(new Usuario());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(datosValidos()));

        assertEquals("Ya existe una cuenta con ese correo", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // registrar: correo vacío
    @Test
    void registrar_correoVacio_lanzaExcepcion() {
        RegistroRequest datos = datosValidos();
        datos.setCorreo("");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(datos));

        assertEquals("El correo es obligatorio", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // claveCorrecta: coincide
    @Test
    void claveCorrecta_coincide_devuelveTrue() {
        Usuario usuario = new Usuario();
        usuario.setClave("HASH");
        when(passwordEncoder.matches("abc123", "HASH")).thenReturn(true);

        assertTrue(usuarioService.claveCorrecta("abc123", usuario));
    }

    // claveCorrecta: no coincide
    @Test
    void claveCorrecta_noCoincide_devuelveFalse() {
        Usuario usuario = new Usuario();
        usuario.setClave("HASH");
        when(passwordEncoder.matches("otra", "HASH")).thenReturn(false);

        assertFalse(usuarioService.claveCorrecta("otra", usuario));
    }
}