package cl.curso.arquitectura.repository;

import cl.curso.arquitectura.model.Libro;
import java.util.List;
import java.util.Optional;

public interface CatalogoRepository {
    List<Libro> todos();
    Optional<Libro> buscar(int id);
    /** Operación atómica: solo descuenta si existe el libro y hay stock. */
    boolean descontarSiDisponible(int libroId, int cantidad);
}
