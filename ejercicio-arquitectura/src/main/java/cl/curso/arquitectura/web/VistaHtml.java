package cl.curso.arquitectura.web;

import cl.curso.arquitectura.model.Libro;
import cl.curso.arquitectura.model.Reserva;
import java.util.List;

public final class VistaHtml {
    private VistaHtml() {}

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace(String.valueOf((char) 34), "&quot;").replace("'", "&#39;");
    }

    public static String plantilla(String titulo, String cuerpo) {
        return "<!doctype html><html lang='es'><head><meta charset='utf-8'>"
            + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
            + "<title>" + escapar(titulo) + "</title>"
            + "<style>body{font:16px system-ui;max-width:800px;margin:36px auto;padding:0 18px;}"
            + "table{border-collapse:collapse;width:100%;}td,th{border:1px solid #bbb;padding:9px;text-align:left;}"
            + "input,button{padding:9px;margin:4px;}a{color:#16727a;}</style></head><body>"
            + "<h1>Biblioteca: arquitectura de software</h1><nav><a href='/'>Catálogo</a> | "
            + "<a href='/reservas'>Reservas</a></nav><hr>" + cuerpo + "</body></html>";
    }

    public static String inicio(List<Libro> libros) {
        StringBuilder html = new StringBuilder("<h2>Catálogo</h2><table>"
            + "<tr><th>ID</th><th>Libro</th><th>Disponibles</th></tr>");
        for (Libro l : libros) html.append("<tr><td>").append(l.id()).append("</td><td>")
            .append(escapar(l.titulo())).append("</td><td>").append(l.disponibles()).append("</td></tr>");
        html.append("</table><h2>Crear reserva</h2><form method='post' action='/reservas'>"
            + "<label>ID de libro <input type='number' min='1' name='libroId' required></label>"
            + "<label>Cantidad <input type='number' min='1' name='cantidad' required></label>"
            + "<button type='submit'>Reservar</button></form>");
        return plantilla("Catálogo", html.toString());
    }

    public static String listado(List<Reserva> reservas) {
        StringBuilder html = new StringBuilder("<h2>Reservas registradas</h2><ul>");
        for (Reserva r : reservas) html.append("<li>Reserva #").append(r.id())
            .append(" — Libro ").append(r.libroId()).append(" — Cantidad ")
            .append(r.cantidad()).append("</li>");
        return plantilla("Reservas", html.append("</ul>").toString());
    }

    public static String mensaje(String titulo, String detalle) {
        return plantilla(titulo, "<h2>" + escapar(titulo) + "</h2><p>" + escapar(detalle) + "</p>");
    }
}
