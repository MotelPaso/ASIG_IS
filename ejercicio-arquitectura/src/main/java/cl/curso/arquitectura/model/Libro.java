package cl.curso.arquitectura.model;

/** Inmutable: los cambios de stock producen una nueva instancia. */
public record Libro(int id, String titulo, int disponibles) {
    public Libro {
        if (id <= 0) throw new IllegalArgumentException("id inválido");
        if (titulo == null || titulo.isBlank()) throw new IllegalArgumentException("título vacío");
        if (disponibles < 0) throw new IllegalArgumentException("stock negativo");
    }
}
