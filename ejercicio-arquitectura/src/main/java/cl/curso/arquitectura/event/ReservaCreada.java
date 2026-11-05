package cl.curso.arquitectura.event;

public record ReservaCreada(long reservaId, int libroId, int cantidad) {}
