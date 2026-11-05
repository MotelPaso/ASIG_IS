package cl.curso.arquitectura.model;

public record Reserva(long id, int libroId, int cantidad) {
    public Reserva {
        if (id <= 0 || libroId <= 0 || cantidad <= 0)
            throw new IllegalArgumentException("reserva inválida");
    }
}
