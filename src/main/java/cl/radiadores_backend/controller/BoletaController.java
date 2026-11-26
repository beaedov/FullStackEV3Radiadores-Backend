package cl.radiadores_backend.controller;

import cl.radiadores_backend.dto.ItemCompra;
import cl.radiadores_backend.model.Boleta;
import cl.radiadores_backend.model.DetalleBoleta;
import cl.radiadores_backend.model.Usuario;
import cl.radiadores_backend.service.BoletaService;
import cl.radiadores_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
public class BoletaController {

    private final BoletaService boletaService;
    private final UsuarioService usuarioService;

    public BoletaController(BoletaService boletaService, UsuarioService usuarioService) {
        this.boletaService = boletaService;
        this.usuarioService = usuarioService;
    }

    // Lista de ordenes (Admin y Vendedor)
    @GetMapping
    public List<Boleta> listar() {
        return boletaService.listar();
    }

    // Detalle de una orden (Admin y Vendedor)
    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleBoleta>> detalles(@PathVariable Long id) {
        if (boletaService.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(boletaService.detalles(id));
    }

    @PostMapping
    public ResponseEntity<?> comprar(@RequestBody List<ItemCompra> items, Authentication auth) {
        Usuario usuario = usuarioService.buscarPorCorreo(auth.getName()); // auth.getName() = correo del token
        try {
            return ResponseEntity.status(201).body(boletaService.comprar(usuario, items));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // sin stock, carrito vacío, etc.
        }
    }
}