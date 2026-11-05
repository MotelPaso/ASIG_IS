package cl.curso.arquitectura.event;

@FunctionalInterface
public interface SuscriptorReserva {
    void alCrearReserva(ReservaCreada evento);
}
