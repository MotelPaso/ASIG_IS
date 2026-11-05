package cl.curso.arquitectura.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Pub/Sub local y síncrono, NO es un broker ni un microservicio. */
public final class BusEventosLocal implements PublicadorEventos {
    private final List<SuscriptorReserva> suscriptores = new CopyOnWriteArrayList<>();

    public void suscribir(SuscriptorReserva suscriptor) {
        if (suscriptor == null) throw new IllegalArgumentException("suscriptor nulo");
        suscriptores.add(suscriptor);
    }

    @Override
    public void publicar(ReservaCreada evento) {
        // La reserva no se anula por un fallo de notificación.
        for (SuscriptorReserva s : suscriptores) {
            try {
                s.alCrearReserva(evento);
            } catch (RuntimeException ex) {
                System.err.println("Notificación fallida: " + ex.getMessage());
            }
        }
    }
}
