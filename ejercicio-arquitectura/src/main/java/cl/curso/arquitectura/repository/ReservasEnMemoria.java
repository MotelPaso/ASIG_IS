package cl.curso.arquitectura.repository;

import cl.curso.arquitectura.model.Reserva;
import java.util.*;

public final class ReservasEnMemoria implements ReservaRepository {
    private final List<Reserva> reservas = new ArrayList<>();
    private long siguienteId = 1;

    public synchronized Reserva guardar(int libroId, int cantidad) {
        Reserva reserva = new Reserva(siguienteId++, libroId, cantidad);
        reservas.add(reserva);
        return reserva;
    }

    public synchronized List<Reserva> todas() { return List.copyOf(reservas); }
}
