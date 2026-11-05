package cl.curso.arquitectura.web;

import cl.curso.arquitectura.event.BusEventosLocal;
import cl.curso.arquitectura.repository.*;
import cl.curso.arquitectura.service.ReservaService;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.net.URI;
import java.net.http.*;
import static org.junit.Assert.*;

public class BibliotecaWebTest {
    private HttpServer server;
    private HttpClient client;
    private String base;

    @Before public void iniciar() throws Exception {
        var catalogo = new CatalogoEnMemoria();
        var servicio = new ReservaService(catalogo, new ReservasEnMemoria(), new BusEventosLocal());
        server = BibliotecaWeb.iniciar(0, catalogo, servicio);
        base = "http://127.0.0.1:" + server.getAddress().getPort();
        client = HttpClient.newHttpClient();
    }

    @After public void cerrar() { if (server != null) server.stop(0); }

    private HttpResponse<String> get(String ruta) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + ruta)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + "/reservas"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test public void paginaPrincipalEs200() throws Exception {
        assertEquals(200, get("/").statusCode());
        assertTrue(get("/").body().contains("Catálogo"));
    }

    @Test public void crearEs201YListadoMuestraReserva() throws Exception {
        assertEquals(201, post("libroId=1&cantidad=2").statusCode());
        assertTrue(get("/reservas").body().contains("Reserva #1"));
    }

    @Test public void cantidadesInvalidasSon400() throws Exception {
        assertEquals(400, post("libroId=1&cantidad=0").statusCode());
        assertEquals(400, post("libroId=x&cantidad=2").statusCode());
    }

    @Test public void inexistenteEs404() throws Exception {
        assertEquals(404, post("libroId=999&cantidad=1").statusCode());
    }

    @Test public void stockInsuficienteEs409() throws Exception {
        assertEquals(409, post("libroId=3&cantidad=5").statusCode());
    }

    @Test public void rutasDesconocidasSon404() throws Exception {
        assertEquals(404, get("/inexistente").statusCode());
    }
}
