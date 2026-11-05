package cl.curso.arquitectura.service;

import cl.curso.arquitectura.event.PublicadorEventos;
import cl.curso.arquitectura.event.ReservaCreada;
import cl.curso.arquitectura.model.Reserva;
import cl.curso.arquitectura.repository.CatalogoRepository;
import cl.curso.arquitectura.repository.ReservaRepository;
import java.util.List;
import java.util.Objects;

/** Caso de uso: no importa si se invoca desde HTTP, CLI o una app móvil. */
public final class ReservaService {
    private final CatalogoRepository catalogo;
    private final ReservaRepository reservas;
    private final PublicadorEventos eventos;

    public ReservaService(CatalogoRepository catalogo, ReservaRepository reservas, PublicadorEventos eventos) {
        this.catalogo = Objects.requireNonNull(catalogo);
        this.reservas = Objects.requireNonNull(reservas);
        this.eventos = Objects.requireNonNull(eventos);
    }

    public Reserva crear(int libroId, int cantidad) {
            // TODO 1. Validar que cantidad sea > 0.
            // TODO 2. Distinguir LibroNoEncontrado de StockInsuficiente.
            // TODO 3. Descontar stock de forma atómica mediante el repositorio.
            // TODO 4. Guardar reserva y publicar un evento ReservaCreada.
            throw new UnsupportedOperationException("TODO: implementar ReservaService.crear");
    }

    public List<Reserva> todas() { return reservas.todas(); }
}
