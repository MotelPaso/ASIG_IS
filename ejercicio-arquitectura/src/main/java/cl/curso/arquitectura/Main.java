package cl.curso.arquitectura;

import cl.curso.arquitectura.event.BusEventosLocal;
import cl.curso.arquitectura.repository.*;
import cl.curso.arquitectura.service.ReservaService;
import cl.curso.arquitectura.web.BibliotecaWeb;

public final class Main {
    public static void main(String[] args) throws Exception {
        var catalogo = new CatalogoEnMemoria();
        var reservas = new ReservasEnMemoria();
        var bus = new BusEventosLocal();
        bus.suscribir(e -> System.out.printf("[Correo simulado] Reserva %d, libro %d, cantidad %d%n",
                e.reservaId(), e.libroId(), e.cantidad()));
        bus.suscribir(e -> System.out.printf("[Auditoría] Reserva registrada: %d%n", e.reservaId()));
        var servicio = new ReservaService(catalogo, reservas, bus);
        int puerto = args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        var server = BibliotecaWeb.iniciar(puerto, catalogo, servicio);
        System.out.println("Biblioteca disponible en http://localhost:" + server.getAddress().getPort());
    }
}
