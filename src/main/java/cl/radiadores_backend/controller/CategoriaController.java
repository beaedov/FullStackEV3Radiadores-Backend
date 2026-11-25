package cl.radiadores_backend.controller;

import cl.radiadores_backend.model.Categoria;
import cl.radiadores_backend.service.CategoriaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listar();
    }

    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        categoria.setId(null);
        return categoriaService.guardar(categoria);
    }
}
