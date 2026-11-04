package cl.edu.metrics.lexer;

public class DefaultTokenFormatter implements TokenFormatter {
    @Override
    public String format(Token token) {
        return token.type() + "('" + token.lexeme() + "')@" + token.position();
    }
}
