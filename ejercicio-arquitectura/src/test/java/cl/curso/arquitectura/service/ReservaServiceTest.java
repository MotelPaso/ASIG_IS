package cl.curso.arquitectura.service;

import cl.curso.arquitectura.event.*;
import cl.curso.arquitectura.model.*;
import cl.curso.arquitectura.repository.*;
import org.junit.Test;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReservaServiceTest {
    private final CatalogoEnMemoria catalogo = new CatalogoEnMemoria();
    private final ReservasEnMemoria reservas = new ReservasEnMemoria();
    private final BusEventosLocal bus = new BusEventosLocal();
    private final ReservaService servicio = new ReservaService(catalogo, reservas, bus);

    @Test public void descuentoDeStockYRegistro() {
        Reserva r = servicio.crear(1, 2);
        assertEquals(1, r.libroId());
        assertEquals(2, r.cantidad());
        assertEquals(1, catalogo.buscar(1).orElseThrow().disponibles());
        assertEquals(1, servicio.todas().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaCantidadCero() { servicio.crear(1, 0); }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaCantidadNegativa() { servicio.crear(1, -1); }

    @Test(expected = LibroNoEncontrado.class)
    public void libroInexistente() { servicio.crear(999, 1); }

    @Test public void stockInsuficienteNoModificaDatos() {
        try { servicio.crear(3, 2); fail("Debió rechazar"); }
        catch (StockInsuficiente expected) { /* correcto */ }
        assertEquals(1, catalogo.buscar(3).orElseThrow().disponibles());
        assertEquals(0, servicio.todas().size());
    }

    @Test public void publicaUnEventoAlCrear() {
        AtomicInteger recibidos = new AtomicInteger();
        bus.suscribir(e -> {
            assertEquals(1, e.libroId());
            recibidos.incrementAndGet();
        });
        servicio.crear(1, 1);
        assertEquals(1, recibidos.get());
    }

    @Test public void suscriptorFallidoNoImpideReserva() {
        bus.suscribir(e -> { throw new IllegalStateException("sin correo"); });
        Reserva r = servicio.crear(1, 1);
        assertTrue(r.id() > 0);
    }

    @Test public void colaboracionesSePuedenVerificarConMockito() {
        CatalogoRepository cat = mock(CatalogoRepository.class);
        ReservaRepository repo = mock(ReservaRepository.class);
        PublicadorEventos pub = mock(PublicadorEventos.class);
        when(cat.buscar(1)).thenReturn(Optional.of(new Libro(1, "Libro", 3)));
        when(cat.descontarSiDisponible(1, 2)).thenReturn(true);
        when(repo.guardar(1, 2)).thenReturn(new Reserva(10, 1, 2));
        var casoUso = new ReservaService(cat, repo, pub);
        assertEquals(10L, casoUso.crear(1, 2).id());
        verify(cat).descontarSiDisponible(1, 2);
        verify(repo).guardar(1, 2);
        verify(pub).publicar(new ReservaCreada(10, 1, 2));
    }

    @Test public void carreraNoPermiteStockNegativo() throws Exception {
        var trabajo = Executors.newFixedThreadPool(2);
        try {
            CountDownLatch inicio = new CountDownLatch(1);
            Callable<Boolean> intento = () -> {
                inicio.await();
                try { servicio.crear(3, 1); return true; }
                catch (StockInsuficiente e) { return false; }
            };
            Future<Boolean> f1 = trabajo.submit(intento);
            Future<Boolean> f2 = trabajo.submit(intento);
            inicio.countDown();
            int exitos = (f1.get(3, TimeUnit.SECONDS) ? 1 : 0)
                        + (f2.get(3, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, exitos);
            assertEquals(0, catalogo.buscar(3).orElseThrow().disponibles());
        } finally { trabajo.shutdownNow(); }
    }
}
