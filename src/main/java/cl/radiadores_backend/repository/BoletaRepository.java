package cl.radiadores_backend.repository;

import cl.radiadores_backend.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
}