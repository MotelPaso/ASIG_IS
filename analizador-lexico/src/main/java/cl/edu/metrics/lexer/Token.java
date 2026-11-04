package cl.edu.metrics.lexer;

/** position es el indice de inicio (base cero) en la cadena original. */
public record Token(TokenType type, String lexeme, int position) { }
