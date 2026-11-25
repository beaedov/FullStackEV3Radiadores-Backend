package cl.radiadores_backend.repository;

import cl.radiadores_backend.model.DetalleBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {
    // Trae todos los detalles de una boleta
    List<DetalleBoleta> findByBoletaId(Long boletaId);
}
