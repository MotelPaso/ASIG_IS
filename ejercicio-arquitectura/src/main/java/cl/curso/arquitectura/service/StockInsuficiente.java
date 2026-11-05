package cl.curso.arquitectura.service;

public final class StockInsuficiente extends RuntimeException {
    public StockInsuficiente(int id) { super("Stock insuficiente para el libro " + id); }
}
