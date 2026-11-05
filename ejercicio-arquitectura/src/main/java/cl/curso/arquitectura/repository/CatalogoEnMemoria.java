package cl.curso.arquitectura.repository;

import cl.curso.arquitectura.model.Libro;
import java.util.*;

public final class CatalogoEnMemoria implements CatalogoRepository {
    private final Map<Integer, Libro> libros = new HashMap<>();

    public CatalogoEnMemoria() {
        libros.put(1, new Libro(1, "Ingeniería de software", 3));
        libros.put(2, new Libro(2, "Diseño de software", 2));
        libros.put(3, new Libro(3, "Sistemas distribuidos", 1));
    }

    public synchronized List<Libro> todos() {
        return libros.values().stream().sorted(Comparator.comparingInt(Libro::id)).toList();
    }

    public synchronized Optional<Libro> buscar(int id) {
        return Optional.ofNullable(libros.get(id));
    }

    public synchronized boolean descontarSiDisponible(int libroId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("cantidad inválida");
        Libro actual = libros.get(libroId);
        if (actual == null || actual.disponibles() < cantidad) return false;
        libros.put(libroId, new Libro(actual.id(), actual.titulo(), actual.disponibles() - cantidad));
        return true;
    }
}
