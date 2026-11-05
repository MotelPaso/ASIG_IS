package cl.curso.arquitectura.web;

import cl.curso.arquitectura.repository.CatalogoRepository;
import cl.curso.arquitectura.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Adaptador HTTP. No modifica el repositorio: delega en ReservaService. */
public final class BibliotecaWeb {
    private BibliotecaWeb() {}

    public static HttpServer iniciar(int puerto, CatalogoRepository catalogo, ReservaService servicio)
            throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", puerto), 0);
        server.createContext("/", exchange -> atender(exchange, catalogo, servicio));
        server.start();
        return server;
    }

    private static void atender(HttpExchange ex, CatalogoRepository catalogo, ReservaService servicio)
            throws IOException {
        String ruta = ex.getRequestURI().getPath();
        String metodo = ex.getRequestMethod();
        try {
            if (ruta.equals("/") || ruta.equals("/libros")) {
                if (!metodo.equals("GET")) { responder(ex, 405, VistaHtml.mensaje("Método no permitido", metodo)); return; }
                responder(ex, 200, VistaHtml.inicio(catalogo.todos()));
            } else if (ruta.equals("/reservas") && metodo.equals("GET")) {
                responder(ex, 200, VistaHtml.listado(servicio.todas()));
            } else if (ruta.equals("/reservas") && metodo.equals("POST")) {
                crearReserva(ex, servicio);
            } else if (ruta.equals("/reservas")) {
                responder(ex, 405, VistaHtml.mensaje("Método no permitido", metodo));
            } else if (ruta.equals("/salud") && metodo.equals("GET")) {
                responder(ex, 200, VistaHtml.mensaje("Estado", "OK"));
            } else {
                responder(ex, 404, VistaHtml.mensaje("No encontrada", ruta));
            }
        } finally { ex.close(); }
    }

    private static void crearReserva(HttpExchange ex, ReservaService servicio) throws IOException {
            // TODO 5. Leer y validar los parámetros del formulario con leerFormulario().
            // TODO 6. Invocar servicio.crear() y construir respuesta 201.
            // TODO 7. Mapear errores: 400, 404, 409; no propagar excepción HTTP.
            responder(ex, 501, VistaHtml.mensaje("No implementado", "Completar POST /reservas"));
    }

    private static Map<String, String> leerFormulario(String body) {
        Map<String, String> campos = new HashMap<>();
        if (body.isBlank()) return campos;
        for (String par : body.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) {
                campos.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return campos;
    }

    private static void responder(HttpExchange ex, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
    }
}
