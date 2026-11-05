package cl.curso.arquitectura.service;

public final class LibroNoEncontrado extends RuntimeException {
    public LibroNoEncontrado(int id) { super("No existe el libro " + id); }
}
