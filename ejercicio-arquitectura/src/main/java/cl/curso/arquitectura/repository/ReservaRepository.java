package cl.curso.arquitectura.repository;

import cl.curso.arquitectura.model.Reserva;
import java.util.List;

public interface ReservaRepository {
    Reserva guardar(int libroId, int cantidad);
    List<Reserva> todas();
}
