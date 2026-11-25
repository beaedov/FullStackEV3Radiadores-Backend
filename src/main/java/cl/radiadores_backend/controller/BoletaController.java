package cl.radiadores_backend.controller;

import cl.radiadores_backend.model.Boleta;
import cl.radiadores_backend.model.DetalleBoleta;
import cl.radiadores_backend.service.BoletaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
public class BoletaController {

    private final BoletaService boletaService;

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
    }

    // Lista de órdenes (admin y vendedor)
    @GetMapping
    public List<Boleta> listar() {
        return boletaService.listar();
    }

    // Detalle de una orden: sus productos
    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleBoleta>> detalles(@PathVariable Long id) {
        if (boletaService.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(boletaService.detalles(id));
    }
}
