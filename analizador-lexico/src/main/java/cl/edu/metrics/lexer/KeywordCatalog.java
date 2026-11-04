package cl.edu.metrics.lexer;

/** Dependencia externa reemplazable por un mock en las pruebas. */
public interface KeywordCatalog {
    boolean isKeyword(String candidate);
}
