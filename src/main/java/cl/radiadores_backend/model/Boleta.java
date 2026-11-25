package cl.radiadores_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Boleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private Integer total;

    // muchas boletas pertenecen a un usuario
    @ManyToOne
    private Usuario usuario;
}
